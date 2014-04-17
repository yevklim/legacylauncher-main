package com.turikhay.tlauncher.ui.loc;

import com.turikhay.tlauncher.ui.center.CenterPanel;
import com.turikhay.tlauncher.ui.text.CheckableTextField;

public abstract class LocalizableCheckableTextField extends CheckableTextField
		implements LocalizableComponent {
	private static final long serialVersionUID = 1L;

	private String placeholderPath;

	private LocalizableCheckableTextField(CenterPanel panel,
			String placeholderPath, String value) {
		super(panel, null, null);

		this.placeholderPath = placeholderPath;
		this.setValue(value);
	}

	public LocalizableCheckableTextField(CenterPanel panel,
			String placeholderPath) {
		this(panel, placeholderPath, null);
	}

	public LocalizableCheckableTextField(String placeholderPath, String value) {
		this(null, placeholderPath, value);
	}

	public LocalizableCheckableTextField(String placeholderPath) {
		this(null, placeholderPath, null);
	}

	@Override
	public void setPlaceholder(String placeholderPath) {
		this.placeholderPath = placeholderPath;
		super.setPlaceholder((Localizable.get() == null) ? placeholderPath
				: Localizable.get().get(placeholderPath));
	}

	public String getPlaceholderPath() {
		return this.placeholderPath;
	}

	@Override
	public void updateLocale() {
		this.setPlaceholder(placeholderPath);
	}
}
