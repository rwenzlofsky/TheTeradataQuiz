package com.dwhpro.model;

public class HACategory {
	private String categoryColor;
	private String categoryId;
	private String categoryName;
	private String leaderBoardId;
	private String categoryDescription;
	private String categoryImagePath;
	private String categoryQuestionLimit;
	private String productIdentifier;
	private Boolean timerRequired;
	public HACategory(){
		
	}
public HACategory(String categoryColor, String categoryId, String categoryName, String leaderBoardId, String categoryDescription,
				  String categoryImagePath, String categoryQuestionLimit, Boolean timerRequired, String productIdentifier){
		this.categoryColor =  categoryColor;
		this.categoryId =  categoryId;
		this.categoryName =  categoryName;
		this.leaderBoardId =  leaderBoardId;
		this.categoryDescription =  categoryDescription;
		this.categoryImagePath =  categoryImagePath;
		this.categoryQuestionLimit =  categoryQuestionLimit;
		this.timerRequired = timerRequired;
	    this.productIdentifier = productIdentifier;
	}

	public String getProductIdentifier() {return productIdentifier;}
	public void setProductIdentifier(String productIdentifier) {this.productIdentifier = productIdentifier;}
	public String getCategoryColor() {
		return categoryColor;
	}
	public void setCategoryColor(String categoryColor) {
		this.categoryColor = categoryColor;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getLeaderBoardId() {
		return leaderBoardId;
	}
	public void setLeaderBoardId(String leaderBoardId) {
		this.leaderBoardId = leaderBoardId;
	}
	public String getCategoryDescription() {
		return categoryDescription;
	}
	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
	public String getCategoryImagePath() {
		return categoryImagePath;
	}
	public void setCategoryImagePath(String categoryImagePath) {
		this.categoryImagePath = categoryImagePath;
	}
	public String getCategoryQuestionLimit() {
		return categoryQuestionLimit;
	}
	public void setCategoryQuestionLimit(String categoryQuestionLimit) {
		this.categoryQuestionLimit = categoryQuestionLimit;
	}
	public Boolean getTimerRequired() {
		return timerRequired;
	}
	public void setTimerRequired(Boolean timerRequired) {
		this.timerRequired = timerRequired;
	}
}
