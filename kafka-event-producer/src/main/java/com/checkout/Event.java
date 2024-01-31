package com.checkout;

class Event
{
    private int userId;
    private String postcode;
    private String webpage;
    private long timestamp;

    public Event(int userId, String postcode, String webpage, long timestamp)
    {
        this.setUserId(userId);
        this.setPostcode(postcode);
        this.setWebpage(webpage);
        this.setTimestamp(timestamp);
    }

    @Override
    public String toString()
    {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{");
        jsonBuilder.append("\"userId\":\"").append(getUserId()).append("\",");
        jsonBuilder.append("\"postcode\":\"").append(getPostcode()).append("\",");
        jsonBuilder.append("\"webpage\":\"").append(getWebpage()).append("\",");
        jsonBuilder.append("\"timestamp\":\"").append(getTimestamp()).append("\"");
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getWebpage()
    {
        return webpage;
    }

    public void setWebpage(String webpage)
    {
        this.webpage = webpage;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}