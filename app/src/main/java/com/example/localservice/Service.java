package com.example.localservice;

public class Service {
    private String serviceId;
    private String name;
    private String description;
    private String contactDetails;
    private String imageUrl;

    private String providerId;

    public Service() {
        // Default constructor required for calls to DataSnapshot.getValue(Service.class)
    }

    public Service(String serviceId, String name, String description, String contactDetails, String imageUrl) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.contactDetails = contactDetails;
        this.imageUrl = imageUrl;
        this.providerId = providerId;
    }

    // Getters and Setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProviderId(){
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}


