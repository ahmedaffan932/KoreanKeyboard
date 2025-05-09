// Generated by view binder compiler. Do not edit!
package com.example.koreankeyboard.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.koreankeyboard.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class TopBarBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView btnBack;

  @NonNull
  public final TextView tvScreenName;

  private TopBarBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView btnBack,
      @NonNull TextView tvScreenName) {
    this.rootView = rootView;
    this.btnBack = btnBack;
    this.tvScreenName = tvScreenName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static TopBarBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static TopBarBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.top_bar, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static TopBarBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnBack;
      ImageView btnBack = ViewBindings.findChildViewById(rootView, id);
      if (btnBack == null) {
        break missingId;
      }

      id = R.id.tvScreenName;
      TextView tvScreenName = ViewBindings.findChildViewById(rootView, id);
      if (tvScreenName == null) {
        break missingId;
      }

      return new TopBarBinding((ConstraintLayout) rootView, btnBack, tvScreenName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
