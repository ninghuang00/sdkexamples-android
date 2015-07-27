package com.easemob.applib.widget;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.chatuidemo.R;

public class EMChatMessageList extends RelativeLayout{
	public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
	
	
    protected ListView messageListView;
	private Context context;
    private EMConversation conversation;
    private int chatType;
    private String toChatUsername;
    private MessageAdapter messageAdapter;

	public EMChatMessageList(Context context, AttributeSet attrs, int defStyle) {
//        parseStyle(context, attrs, defStyle);
        this(context, attrs);
    }

    public EMChatMessageList(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	init(context);
    }

    public EMChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.em_chat_message_list, this);
        messageListView = (ListView) findViewById(R.id.list);
    }
    
    /**
     * init widget
     * @param toChatUsername
     * @param chatType
     */
    public void init(String toChatUsername, int chatType) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        
        conversation = EMChatManager.getInstance().getConversation(toChatUsername);
        messageAdapter = new MessageAdapter(context, toChatUsername, chatType, messageListView);
        // 设置adapter显示消息
        messageListView.setAdapter(messageAdapter);
        
        messageAdapter.refreshSelectLast();
        
    }
    
    /**
     * 发送消息
     * @param message 要发送的消息
     */
    public void sendMessage(EMMessage message){
        // 把messgage加到conversation中
        conversation.addMessage(message);
        // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
        messageAdapter.refreshSelectLast();
    }
    
    /**
     * 发送文本消息
     * 
     * @param content
     *            文本内容
     * @param 
     *            扩展属性
     */
    public void sendTextMessage(String content, HashMap<String, Object> attrs) {

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP) {
                message.setChatType(ChatType.GroupChat);
            } else if (chatType == CHATTYPE_CHATROOM) {
                message.setChatType(ChatType.ChatRoom);
            }
            // if(isRobot){
            // message.setAttribute("em_robot_message", true);
            // }
            setAttributes(attrs, message);
            
            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
             
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);
            
            sendMessage(message);

        }
    }
    
    /**
     * 发送语音消息
     * 
     * @param filePath
     * @param fileName
     * @param length
     */
    public void sendVoiceMessage(String filePath, String fileName, int length, HashMap<String, Object> attrs) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP){
                message.setChatType(ChatType.GroupChat);
            }else if(chatType == CHATTYPE_CHATROOM){
                message.setChatType(ChatType.ChatRoom);
            }
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), length);
            message.addBody(body);
            
            setAttributes(attrs, message);
            message.setReceipt(toChatUsername);
            
            sendMessage(message);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 发送图片消息
     * 
     * @param filePath
     */
    public void sendImageMessage(String filePath, boolean sendOriginalImage, HashMap<String, Object> attrs) {
        String to = toChatUsername;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP){
            message.setChatType(ChatType.GroupChat);
        }else if(chatType == CHATTYPE_CHATROOM){
            message.setChatType(ChatType.ChatRoom);
        }
        
        setAttributes(attrs, message);
        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        body.setSendOriginalImage(sendOriginalImage);
        message.addBody(body);
        
        sendMessage(message);
    }
    
    /**
     * 发送视频消息
     */
    public void sendVideoMessage(String filePath, String thumbPath, int length, HashMap<String, Object> attrs) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP){
                message.setChatType(ChatType.GroupChat);
            }else if(chatType == CHATTYPE_CHATROOM){
                message.setChatType(ChatType.ChatRoom);
            }
            String to = toChatUsername;
            message.setReceipt(to);
            setAttributes(attrs, message);
            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
            message.addBody(body);
            
            sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 发送位置信息
     * 
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    public void sendLocationMessage(double latitude, double longitude, String locationAddress, HashMap<String, Object> attrs) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP){
            message.setChatType(ChatType.GroupChat);
        }else if(chatType == CHATTYPE_CHATROOM){
            message.setChatType(ChatType.ChatRoom);
        }
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(toChatUsername);
        
        setAttributes(attrs, message);
        
        sendMessage(message);
    }
    
    /**
     * 发送文件消息
     * 
     * @param uri
     */
    public void sendFileMessage(Uri uri, HashMap<String, Object> attrs) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(context, R.string.File_does_not_exist, 0).show();
            return;
        }
        //大于10M不让发送
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(context, R.string.The_file_is_not_greater_than_10_m, 0).show();
            return;
        }

        // 创建一个文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP){
            message.setChatType(ChatType.GroupChat);
        }else if(chatType == CHATTYPE_CHATROOM){
            message.setChatType(ChatType.ChatRoom);
        }

        setAttributes(attrs, message);
        message.setReceipt(toChatUsername);
        // add message body
        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
        message.addBody(body);
        
        sendMessage(message);
        
    }

    /**
     * 设置扩展属性
     * @param attrs
     * @param message
     */
    protected void setAttributes(HashMap<String, Object> attrs, EMMessage message) {
        
        if(attrs != null && attrs.size() != 0){
            String[] keys = attrs.keySet().toArray(new String[]{});
            for(int i = 0; i < attrs.size(); i++){
                message.setAttribute(keys[i], attrs.get(keys[i]));
            }
        }
    }
    
    
    
    protected void parseStyle(Context context, AttributeSet attrs, int defStyle) {
    	TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EMChatMessageList, 0, defStyle);
		ta.recycle();
	}

    /**
     * 获取里面的listview
     * @return
     */
	public ListView getListView() {
		return messageListView;
	} 
}
