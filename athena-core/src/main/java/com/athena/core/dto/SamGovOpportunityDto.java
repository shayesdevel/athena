package com.athena.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for parsing SAM.gov opportunity JSON data.
 * Maps to cached JSON files from SAM.gov API responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SamGovOpportunityDto {

    @JsonProperty("noticeId")
    private String noticeId;

    @JsonProperty("solicitationNumber")
    private String solicitationNumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("department")
    private String department;

    @JsonProperty("subTier")
    private String subTier;

    @JsonProperty("office")
    private String office;

    @JsonProperty("postedDate")
    private LocalDate postedDate;

    @JsonProperty("responseDeadLine")
    private LocalDate responseDeadline;

    @JsonProperty("naicsCode")
    private String naicsCode;

    @JsonProperty("setAside")
    private String setAside;

    @JsonProperty("type")
    private String noticeType;

    @JsonProperty("placeOfPerformance")
    private PlaceOfPerformanceDto placeOfPerformance;

    @JsonProperty("classificationCode")
    private String classificationCode;

    @JsonProperty("active")
    private String active;

    @JsonProperty("archive")
    private String archive;

    @JsonProperty("pointOfContact")
    private List<PointOfContactDto> pointOfContact;

    @JsonProperty("organizationType")
    private String organizationType;

    @JsonProperty("additionalInfoLink")
    private String additionalInfoLink;

    @JsonProperty("uiLink")
    private String uiLink;

    // Nested DTOs
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaceOfPerformanceDto {
        @JsonProperty("city")
        private String city;

        @JsonProperty("state")
        private String state;

        @JsonProperty("country")
        private String country;

        @JsonProperty("zip")
        private String zip;

        // Getters and setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PointOfContactDto {
        @JsonProperty("type")
        private String type;

        @JsonProperty("title")
        private String title;

        @JsonProperty("fullName")
        private String fullName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    // Getters and setters
    public String getNoticeId() { return noticeId; }
    public void setNoticeId(String noticeId) { this.noticeId = noticeId; }
    public String getSolicitationNumber() { return solicitationNumber; }
    public void setSolicitationNumber(String solicitationNumber) { this.solicitationNumber = solicitationNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getSubTier() { return subTier; }
    public void setSubTier(String subTier) { this.subTier = subTier; }
    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = office; }
    public LocalDate getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDate postedDate) { this.postedDate = postedDate; }
    public LocalDate getResponseDeadline() { return responseDeadline; }
    public void setResponseDeadline(LocalDate responseDeadline) { this.responseDeadline = responseDeadline; }
    public String getNaicsCode() { return naicsCode; }
    public void setNaicsCode(String naicsCode) { this.naicsCode = naicsCode; }
    public String getSetAside() { return setAside; }
    public void setSetAside(String setAside) { this.setAside = setAside; }
    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }
    public PlaceOfPerformanceDto getPlaceOfPerformance() { return placeOfPerformance; }
    public void setPlaceOfPerformance(PlaceOfPerformanceDto placeOfPerformance) { this.placeOfPerformance = placeOfPerformance; }
    public String getClassificationCode() { return classificationCode; }
    public void setClassificationCode(String classificationCode) { this.classificationCode = classificationCode; }
    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }
    public String getArchive() { return archive; }
    public void setArchive(String archive) { this.archive = archive; }
    public List<PointOfContactDto> getPointOfContact() { return pointOfContact; }
    public void setPointOfContact(List<PointOfContactDto> pointOfContact) { this.pointOfContact = pointOfContact; }
    public String getOrganizationType() { return organizationType; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
    public String getAdditionalInfoLink() { return additionalInfoLink; }
    public void setAdditionalInfoLink(String additionalInfoLink) { this.additionalInfoLink = additionalInfoLink; }
    public String getUiLink() { return uiLink; }
    public void setUiLink(String uiLink) { this.uiLink = uiLink; }
}
