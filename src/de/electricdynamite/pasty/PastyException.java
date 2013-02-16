package de.electricdynamite.pasty;

/*
 *  Copyright 2012 Philipp Geschke
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


public class PastyException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8439075085500583159L;

	public static final short ERROR_UNKNOWN = 0;
	public static final short ERROR_MISC = 1;
	public static final short ERROR_AUTHORIZATION_FAILED = 3;
	public static final short ERROR_ILLEGAL_RESPONSE = 4;
	public static final short ERROR_IO_EXCEPTION = 5;
	public static final short ERROR_NO_CACHE_EXCEPTION = 6;
	public static final short ERROR_DEVICE_NOT_REGISTERED = 7;
	public static final short ERROR_DEVICE_ALREADY_REGISTERED = 8;
	
	public short errorId;
	
	public PastyException() {
	}

	public PastyException(String msg) {
	    super(msg);
	}
	
	public PastyException(short errorId, String msg) {
		super(msg);
		this.errorId = errorId;
	}
	
	public PastyException(short errorId) {
		super();
		this.errorId = errorId;
	}
}
