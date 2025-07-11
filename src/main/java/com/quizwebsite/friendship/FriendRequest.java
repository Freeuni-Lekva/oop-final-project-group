package com.quizwebsite.friendship;

import java.sql.Timestamp;

public class FriendRequest {

    private int requestId;
    private int requesterId;
    private int recipientId;
    private String status;
    private Timestamp requestedAt;
    private String requesterUsername;

    public FriendRequest() {
    }

    public FriendRequest(int requestId, int requesterId, int recipientId, String status, Timestamp requestedAt) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.recipientId = recipientId;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public FriendRequest(int requestId, int requesterId, int recipientId, String status) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.recipientId = recipientId;
        this.status = status;
    }

    public int getRequestId() {
        return this.requestId;
    }

    public String getRequesterUsername() {
        return this.requesterUsername;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }
}