package com.turntable.client.friend;

/** The status of a friendship between two users. */
public enum FriendStatus {

    /** A friend request has been sent but not yet accepted or declined. */
    INVITATION_SENT,
    
    /** A friend request has been received but not yet accepted or declined. */
    INVITATION_RECEIVED,
    
    /** The friend request has been accepted and the friendship is active. */
    ACCEPTED;
}
