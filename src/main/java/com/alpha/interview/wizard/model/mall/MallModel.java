package com.alpha.interview.wizard.model.mall;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.alpha.interview.wizard.constants.mall.constants.ActiveStatusConstants;
import com.alpha.interview.wizard.controller.mall.util.MallUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@ToString
public class MallModel {
	public MallModel(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String country;
	private String city;
	private String name;
	private String description;
	private String displayName;

	private String location;
	@Column(length = 500)
	private String googleMap;
	private String imgUrl;
	private String floors;

	@Column(length = 500)
	private String additionalDetails;
	@Column(length = 30)
	private String timings;
	@Column(length = 20)
	private String contactNumber;
	@Column(length = 50)
	private String contactEmail;
	private int activeStatusKey = ActiveStatusConstants.ACTIVE.getType();
	private Date updateTimestamp;
	private Date createTimestamp;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<WebImage> images;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Shop> shops;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Attraction> attractions;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Parking> parkings;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Event> events;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<OtherAttraction> otherAttractions;

	public Date getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}
	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = MallUtil.formatName(name);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getFloors() {
		return floors;
	}
	public void setFloors(String floors) {
		this.floors = floors;
	}
	public int getActiveStatusKey() {
		return activeStatusKey;
	}
	public void setActiveStatusKey(int activeStatusKey) {
		this.activeStatusKey = activeStatusKey;
	}
	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	public List<Shop> getShops() {
		return shops;
	}
	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}
	public List<Attraction> getAttractions() {
		return attractions;
	}
	public void setAttractions(List<Attraction> attractions) {
		this.attractions = attractions;
	}
	public List<Parking> getParkings() {
		return parkings;
	}
	public void setParkings(List<Parking> parkings) {
		this.parkings = parkings;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public List<OtherAttraction> getOtherAttractions() {
		return otherAttractions;
	}
	public void setOtherAttractions(List<OtherAttraction> otherAttractions) {
		this.otherAttractions = otherAttractions;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public List<WebImage> getImages() {
		return images;
	}
	public void setImages(List<WebImage> images) {
		this.images = images;
	}
	public String getTimings() {
		return timings;
	}
	public void setTimings(String timings) {
		this.timings = timings;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getGoogleMap() {
		return googleMap;
	}
	public void setGoogleMap(String googleMap) {
		this.googleMap = googleMap;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

}
