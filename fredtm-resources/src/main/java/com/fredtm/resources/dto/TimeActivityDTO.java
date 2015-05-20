package com.fredtm.resources.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TimeActivityDTO extends BaseDTO {

	private String activityId;
	private String activityTitle;
	private String collectId;
	private long finalDate = 0l;
	private long startDate = 0l;
	private long timed = 0l;
	private int collectedAmount;

	public TimeActivityDTO() {

	}

	public TimeActivityDTO uuid(String uuid) {
		setUuid(uuid);
		return this;
	}

	public TimeActivityDTO activityTitle(String title) {
		this.activityTitle = title;
		return this;
	}

	public TimeActivityDTO collectId(String value) {
		this.collectId = value;
		return this;
	}

	public TimeActivityDTO activityId(String value) {
		this.activityId = value;
		return this;
	}

	public TimeActivityDTO finalDate(long value) {
		this.finalDate = value;
		return this;
	}

	public TimeActivityDTO startDate(long value) {
		this.startDate = value;
		return this;
	}

	public TimeActivityDTO timed(long value) {
		this.timed = value;
		return this;
	}

	public TimeActivityDTO collectedAmount(int value) {
		this.collectedAmount = value;
		return this;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getCollectId() {
		return collectId;
	}

	public void setCollectId(String collectId) {
		this.collectId = collectId;
	}

	
	public long getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(long finalDate) {
		this.finalDate = finalDate;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getTimed() {
		return timed;
	}

	public void setTimed(long timed) {
		this.timed = timed;
	}

	public int getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(int collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

	public String getActivityTitle() {
		return activityTitle;
	}

	public void setActivityTitle(String activityTitle) {
		this.activityTitle = activityTitle;
	}
	
	

	@Override
	public String toString() {
		return "TimeActivityResource [activityId=" + activityId
				+ ", activityTitle=" + activityTitle + ", collectId="
				+ collectId + ", finalDate=" + finalDate + ", startDate="
				+ startDate + ", timed=" + timed + ", collectedAmount="
				+ collectedAmount + ", getUuid()=" + getUuid()
				+ ", getLinks()=" + getLinks() + "]";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getUuid()).append(activityId)
				.append(collectId).append(finalDate).append(startDate)
				.append(timed).build();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeActivityDTO other = (TimeActivityDTO) obj;
		return new EqualsBuilder().append(getUuid(), other.getUuid())
				.append(activityId, other.activityId)
				.append(collectId, other.collectId)
				.append(finalDate, other.finalDate)
				.append(startDate, other.startDate).append(timed, other.timed)
				.isEquals();
	}

}