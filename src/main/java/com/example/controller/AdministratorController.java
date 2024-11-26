package com.example.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.domain.Administrator;
import com.example.form.InsertAdministratorForm;
import com.example.form.LoginForm;
import com.example.service.AdministratorService;

import jakarta.servlet.http.HttpSession;

/**
 * 管理者情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/")
public class AdministratorController {

	@Autowired
	private AdministratorService administratorService;

	@Autowired
	private HttpSession session;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public InsertAdministratorForm setUpInsertAdministratorForm() {
		return new InsertAdministratorForm();
	}

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public LoginForm setUpLoginForm() {
		return new LoginForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：管理者を登録する
	/////////////////////////////////////////////////////
	/**
	 * 管理者登録画面を出力します.
	 * 
	 * @return 管理者登録画面
	 */

	/*
	 * (1-1)初級 画⾯遷移
	 * 従業員⼀覧画⾯をログイン画面へ変更しました。
	 */
	@GetMapping("/toInsert")
	public String toInsert() {
		return "administrator/insert";
	}

	/*
	 * (1-2)初級 入力値エラー処理
	 * 入力値エラーの機能を追加しました。
	 */

	@PostMapping("/insert")
	public String insertAdministrator(
			@Validated InsertAdministratorForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {

		/*
		 * もしエラーが一つでもあれば入力画面に遷移します。
		 * エラーメッセージを表示します。
		 */
		if (bindingResult.hasErrors()) {
			return "administrator/insert";
		}

		/**
		 * (1-3) 中級 Eメール重複チェックを改修しました。これによって既に登録してあるＥメールアドレスは使えません。
		 */
		Administrator mail2 = administratorService.findByMailAddress(form.getMailAddress());
		if ( mail2 != null) {
			bindingResult.rejectValue("mailAddress", "error.mailAddress.alreadyExists", "このメールアドレスは既に使用されています。");
			return "administrator/insert";

		}

		/*
		 * (1-5) 中級 確認用パスワードを追加しました。これによってパスワードが一致しないと登録できないようになりました。
		 */

		    if (!form.getPassword().equals(form.getPassword2())) {
            bindingResult.rejectValue("password2", "error.password.mismatch","パスワードが一致しません");
            return "administrator/insert"; // 不一致の場合はログインページに戻る
        }

		
		

		/**
		 * 管理者情報を登録します.
		 * 
		 * @param form 管理者情報用フォーム
		 * @return ログイン画面へリダイレクト
		 */
		Administrator administrator = new Administrator();
		/*
		 * フォームオブジェクトからドメインオブジェクトにプロパティ値をコピー
		 */
		BeanUtils.copyProperties(form, administrator);

		administratorService.insert(administrator);

		/*
		 * (1-4)初級 ダブルサブミット（リロード対策）を改修しました。
		 */

		return "redirect:/";
		/*
		 * (1-2)初級入力値エラー改修
		 * 必要のないflashスコープを削除しました。
		 */
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログインをする
	/////////////////////////////////////////////////////
	/**
	 * ログイン画面を出力します.
	 * 
	 * @return ログイン画面
	 */

	/*
	 * (1-4)初級 ダブルサブミット（リロード対策）を改修しました。
	 */
	@GetMapping("")
	public String toLogin() {
		return "administrator/login";
	}

	/**
	 * ログインします.
	 * 
	 * @param form 管理者情報用フォーム
	 * @return ログイン後の従業員一覧画面
	 */
	@PostMapping("/login")
	public String login(LoginForm form, RedirectAttributes redirectAttributes) {

		Administrator administrator = administratorService.login(form.getMailAddress(), form.getPassword());

		if (administrator == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "メールアドレスまたはパスワードが不正です。");
			return "redirect:/administrator/login";
		}

		/*
		 *(2-1)中級 ログイン者名表示でログインしたユーザーを表示できるようにしました。
		 */ 
		session.setAttribute("administratorName", administrator.getName());

		return "redirect:/employee/showList"; // (1-1) 初級遷移 loginからemployeeに修正しました。

	}

	/////////////////////////////////////////////////////
	// ユースケース：ログアウトをする
	/////////////////////////////////////////////////////
	/**
	 * ログアウトをします. (SpringSecurityに任せるためコメントアウトしました)
	 * 
	 * @return ログイン画面
	 */
	@GetMapping(value = "/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}

}
