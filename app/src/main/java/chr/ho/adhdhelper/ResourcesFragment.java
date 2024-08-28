package chr.ho.adhdhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResourcesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        TextView linkOne = view.findViewById(R.id.link_one);
        TextView linkTwo = view.findViewById(R.id.link_two);
        TextView linkThree = view.findViewById(R.id.link_three);

        linkOne.setOnClickListener(v -> openWebPage("https://healthunlocked.com/adult-ADHD"));
        linkTwo.setOnClickListener(v -> openWebPage("https://healthunlocked.com/adhd-parents"));
        linkThree.setOnClickListener(v -> openWebPage("https://chadd.org/about-adhd/overview/"));

        return view;
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
