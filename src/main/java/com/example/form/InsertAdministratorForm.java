package com.example.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 管理者情報登録時に使用するフォーム.
 * 
 * @author igamasayuki
 * 
 */
public class InsertAdministratorForm {

	/*
	 * 入力値エラー処理で
	 * バリデーションを追加しました。
	 */
	/** 名前 */
	@NotBlank(message="名前は必須です。")
	private String name;
	/** メールアドレス */
	@NotBlank(message="メールアドレスの入力は必須です。")
	@Size(min=12, max=64, message="メールアドレスは12文字以上64文字以内で記載してください。")
	@Email(message="メールアドレスの形式が不正です。")
	private String mailAddress;
	/** パスワード */
	@NotBlank(message="パスワードの入力は必須です。")
	@Size(min=8, max=24, message="パスワードは8文字以上24文字以内で記載してください。")
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "InsertAdministratorForm [name=" + name + ", mailAddress=" + mailAddress + ", password=" + password
				+ "]";
	}

}
