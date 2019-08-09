package com.example.loldashboard.exception;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ResponseCodeChecker {
	private int responsCode;
	private String message;

	public ResponseCodeChecker() {
		this.responsCode = 0;
		this.message = "";
	}

	public boolean checker() {
		if (this.responsCode == 200) {
			this.message = "Response Succees!";
			return true;
		} else if (this.responsCode == 400) {
			this.message = "ERROR : Response Bad request!";
		} else if (this.responsCode == 401) {
			this.message = "ERROR : Response Unauthorized!";
		} else if (this.responsCode == 403) {
			this.message = "ERROR : Response Forbidden!";
		} else if (this.responsCode == 404) {
			this.message = "ERROR : Response Data not found!";
		} else if (this.responsCode == 405) {
			this.message = "ERROR : Response Method not allowed!";
		} else if (this.responsCode == 415) {
			this.message = "ERROR : Response Unsupported media type!";
		} else if (this.responsCode == 429) {
			this.message = "ERROR : Response Rate limit exceeded!";
		} else if (this.responsCode == 500) {
			this.message = "ERROR : Response Internal server error!";
		} else if (this.responsCode == 502) {
			this.message = "ERROR : Response Bad gateway!";
		} else if (this.responsCode == 503) {
			this.message = "ERROR : Response Service unavailable!";
		} else if (this.responsCode == 504) {
			this.message = "ERROR : Response Gateway timeout!";
		}
		return false;
	}

}
