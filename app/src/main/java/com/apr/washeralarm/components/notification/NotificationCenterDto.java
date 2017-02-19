package com.apr.washeralarm.components.notification;

import android.net.Uri;

public class NotificationCenterDto
{

    private int idNotification;
    private String title;
    private String text;
    private String info;
    private int smallIcon;
    private Uri soundUri;
    private int type;
    private String email;

    private NotificationCenterDto(int idNotification, String title, String text, String info, int smallIcon, Uri defaultSoundUri, int type)
    {
        this.idNotification = idNotification;
        this.title = title;
        this.text = text;
        this.info = info;
        this.smallIcon = smallIcon;
        this.soundUri = defaultSoundUri;
        this.type = type;
    }

    private NotificationCenterDto(int type, String title, String text, String info, String email)
    {
        this.type = type;
        this.title = title;
        this.text = text;
        this.info = info;
        this.email = email;
    }

    public int getIdNotification()
    {
        return idNotification;
    }

    public String getTitle()
    {
        return title;
    }

    public String getInfo()
    {
        return info;
    }

    public String getText()
    {
        return text;
    }

    public int getIcon()
    {
        return smallIcon;
    }

    public int getSmallIcon()
    {
        return smallIcon;
    }

    public Uri getSoundUri()
    {
        return soundUri;
    }

    public int getType()
    {
        return type;
    }

    public String getEmail()
    {
        return email;
    }

    public static class NotificationBuilder
    {
        private int nestedIdNotification;
        private String nestedTitle;
        private String nestedText;
        private String nestedInfo;
        private int nestedSmallIcon;
        private Uri nestedSoundUri;
        private int nestedType;
        private String nestedEmail;

        public NotificationBuilder(final int newIdNotification, final String newTitle, final String newText, final int newType)
        {
            this.nestedIdNotification = newIdNotification;
            this.nestedTitle = newTitle;
            this.nestedText = newText;
            this.nestedType = newType;
        }

        public NotificationBuilder idNotification(int newIdNotification)
        {
            this.nestedIdNotification = newIdNotification;
            return this;
        }

        public NotificationBuilder title(String newTitle)
        {
            this.nestedTitle = newTitle;
            return this;
        }

        public NotificationBuilder text(String newText)
        {
            this.nestedText = newText;
            return this;
        }

        public NotificationBuilder info(String newInfo)
        {
            this.nestedInfo = newInfo;
            return this;
        }

        public NotificationBuilder smallIcon(int newSmallIcon)
        {
            this.nestedSmallIcon = newSmallIcon;
            return this;
        }

        public NotificationBuilder sound(Uri newSound)
        {
            this.nestedSoundUri = newSound;
            return this;
        }
        public NotificationBuilder type(int newType)
        {
            this.nestedType = newType;
            return this;
        }

        public NotificationBuilder email(String newEmail)
        {
            this.nestedEmail = newEmail;
            return this;
        }

        public NotificationCenterDto buildLocal()
        {
            return new NotificationCenterDto(nestedIdNotification,
                    nestedTitle,
                    nestedText,
                    nestedInfo,
                    nestedSmallIcon,
                    nestedSoundUri,
                    nestedType);
        }
        public NotificationCenterDto buildExternal()
        {
            return new NotificationCenterDto(nestedType,
                    nestedTitle,
                    nestedText,
                    nestedInfo,
                    nestedEmail);
        }
    }
}


