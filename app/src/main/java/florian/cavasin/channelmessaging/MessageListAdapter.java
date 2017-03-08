package florian.cavasin.channelmessaging;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Environment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.List;

        import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends ArrayAdapter<Message> {
    private final Context context;
    private final List<Message> values;

    public MessageListAdapter(Context context, List<Message> messages ){
        super(context, 0, messages);
        this.context = context;
        this.values = messages;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        SharedPreferences settings = context.getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String username = settings.getString("username","");



        //TODO : Egalite à revoir
        if(message.getUsername() != username) {
            if(message.getMessageImageUrl().equals("")){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message_layout, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message_image_layout, parent, false);
            }
        } else {
            if(message.getMessageImageUrl().equals("")){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message_layout_right, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message_image_layout, parent, false);
            }
        }


        TextView user = (TextView) convertView.findViewById(R.id.textViewUser);
        TextView date = (TextView) convertView.findViewById(R.id.textViewDate);
        CircleImageView avatar = (CircleImageView) convertView.findViewById(R.id.imageViewAvatar);
        user.setText(message.getUsername().toString() + " : ");
        date.setText(message.getDate());

        if(ChannelActivity.images.containsKey(message.getImageUrl())){
            avatar.setImageBitmap(ChannelActivity.images.get(message.getImageUrl()));
        }else{
            new DownloadImageTask(avatar).execute(message.getImageUrl());
        }

        if(message.getMessageImageUrl().equals("")){
            TextView text = (TextView) convertView.findViewById(R.id.textViewMessage);
            text.setText(message.getMessage());
        } else{
            ImageView messageImage = (ImageView) convertView.findViewById(R.id.imageViewImageMessage);

            if(ChannelActivity.images.containsKey(message.getMessageImageUrl())){
                messageImage.setImageBitmap(ChannelActivity.images.get(message.getMessageImageUrl()));
            }else{
                new DownloadImageTask(messageImage).execute(message.getMessageImageUrl());
            }
        }


        return convertView;
    }
}