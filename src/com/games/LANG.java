package com.games;

	public enum LANG {
		SPANISH("s"), ENGLISH("e");
		
		private final String value;
		
		private LANG(String s) {
			this.value = s;
		}
		
		public String value() {
			return this.value;
		}
		
		public static LANG getEnum(String name) {
			for(LANG l : values()) {
				if(l.value().equals(name)) {
					return l;
				}
			}
			return null; 
		}
}
