package com.medeat.pdf.dto.medi;

public class MediDisclaimerDto {

    private String dataSource;   // PGHD
    private String disclaimer1;
    private String disclaimer2;
    private String appName;      // MEDI_EAT
    
    private String content;   // 면책 문구 본문
    private String source;    // 데이터 출처 (선택)
    
    public MediDisclaimerDto() {
		// TODO Auto-generated constructor stub
	}

	

	public MediDisclaimerDto(String dataSource, String disclaimer1, String disclaimer2, String appName, String content,
			String source) {
		super();
		this.dataSource = dataSource;
		this.disclaimer1 = disclaimer1;
		this.disclaimer2 = disclaimer2;
		this.appName = appName;
		this.content = content;
		this.source = source;
	}



	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDisclaimer1() {
		return disclaimer1;
	}

	public void setDisclaimer1(String disclaimer1) {
		this.disclaimer1 = disclaimer1;
	}

	public String getDisclaimer2() {
		return disclaimer2;
	}

	public void setDisclaimer2(String disclaimer2) {
		this.disclaimer2 = disclaimer2;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	

	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}



	@Override
	public String toString() {
		return "MediDisclaimerDto [dataSource=" + dataSource + ", disclaimer1=" + disclaimer1 + ", disclaimer2="
				+ disclaimer2 + ", appName=" + appName + ", content=" + content + ", source=" + source + "]";
	}



	
    
    
}
