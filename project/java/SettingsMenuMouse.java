/*
Simple DirectMedia Layer
Java source code (C) 2009-2012 Sergii Pylypenko
  
This software is provided 'as-is', without any express or implied
warranty.  In no event will the authors be held liable for any damages
arising from the use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:
  
1. The origin of this software must not be misrepresented; you must not
   claim that you wrote the original software. If you use this software
   in a product, an acknowledgment in the product documentation would be
   appreciated but is not required. 
2. Altered source versions must be plainly marked as such, and must not be
   misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.
*/

package net.sourceforge.clonekeenplus;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;
import java.io.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.StatFs;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.Collections;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.lang.String;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.text.Editable;
import android.text.SpannedString;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.util.DisplayMetrics;
import android.net.Uri;
import java.util.concurrent.Semaphore;
import java.util.Arrays;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.widget.Toast;


class SettingsMenuMouse extends SettingsMenu
{
	static class MouseConfigMainMenu extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.mouse_emulation);
		}
		boolean enabled()
		{
			return Globals.AppUsesMouse;
		}
		void run (final MainActivity p)
		{
			Menu options[] =
			{
				new DisplaySizeConfig(false),
				new LeftClickConfig(),
				new RightClickConfig(),
				new AdditionalMouseConfig(),
				new JoystickMouseConfig(),
				new TouchPressureMeasurementTool(),
				new CalibrateTouchscreenMenu(),
				new OkButton(),
			};
			showMenuOptionsList(p, options);
		}
	}

	static class DisplaySizeConfig extends Menu
	{
		boolean firstStart = false;
		DisplaySizeConfig()
		{
			this.firstStart = true;
		}
		DisplaySizeConfig(boolean firstStart)
		{
			this.firstStart = firstStart;
		}
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.display_size_mouse);
		}
		void run (final MainActivity p)
		{
			CharSequence[] items = {
										p.getResources().getString(R.string.display_size_small),
										p.getResources().getString(R.string.display_size_small_touchpad),
										p.getResources().getString(R.string.display_size_large),
									};
			int _size_small = 0;
			int _size_small_touchpad = 1;
			int _size_large = 2;
			int _more_options = 3;

			if( ! Globals.SwVideoMode )
			{
				CharSequence[] items2 = {
											p.getResources().getString(R.string.display_size_small_touchpad),
											p.getResources().getString(R.string.display_size_large),
										};
				items = items2;
				_size_small_touchpad = 0;
				_size_large = 1;
				_size_small = 1000;
			}
			if( firstStart )
			{
				CharSequence[] items2 = {
											p.getResources().getString(R.string.display_size_small),
											p.getResources().getString(R.string.display_size_small_touchpad),
											p.getResources().getString(R.string.display_size_large),
											p.getResources().getString(R.string.show_more_options),
										};
				items = items2;
				if( ! Globals.SwVideoMode )
				{
					CharSequence[] items3 = {
												p.getResources().getString(R.string.display_size_small_touchpad),
												p.getResources().getString(R.string.display_size_large),
												p.getResources().getString(R.string.show_more_options),
											};
					items = items3;
					_more_options = 3;
				}
			}
			// Java is so damn worse than C++11
			final int size_small = _size_small;
			final int size_small_touchpad = _size_small_touchpad;
			final int size_large = _size_large;
			final int more_options = _more_options;

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.display_size);
			class ClickListener implements DialogInterface.OnClickListener
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();
					if( item == size_large )
					{
						Globals.LeftClickMethod = Mouse.LEFT_CLICK_NORMAL;
						Globals.RelativeMouseMovement = false;
						Globals.ShowScreenUnderFinger = Mouse.ZOOM_NONE;
					}
					if( item == size_small )
					{
						Globals.LeftClickMethod = Mouse.LEFT_CLICK_NEAR_CURSOR;
						Globals.RelativeMouseMovement = false;
						Globals.ShowScreenUnderFinger = Mouse.ZOOM_MAGNIFIER;
					}
					if( item == size_small_touchpad )
					{
						Globals.LeftClickMethod = Mouse.LEFT_CLICK_WITH_TAP_OR_TIMEOUT;
						Globals.RelativeMouseMovement = true;
						Globals.ShowScreenUnderFinger = Mouse.ZOOM_NONE;
					}
					if( item == more_options )
					{
						menuStack.clear();
						new MainMenu().run(p);
						return;
					}
					goBack(p);
				}
			}
			builder.setItems(items, new ClickListener());
			/*
			else
				builder.setSingleChoiceItems(items,
					Globals.ShowScreenUnderFinger == Mouse.ZOOM_NONE ?
					( Globals.RelativeMouseMovement ? Globals.SwVideoMode ? 2 : 1 : 0 ) :
					( Globals.ShowScreenUnderFinger == Mouse.ZOOM_MAGNIFIER && Globals.SwVideoMode ) ? 1 :
					Globals.ShowScreenUnderFinger + 1,
					new ClickListener());
			*/
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class LeftClickConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.leftclick_question);
		}
		void run (final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.leftclick_normal),
											p.getResources().getString(R.string.leftclick_near_cursor),
											p.getResources().getString(R.string.leftclick_multitouch),
											p.getResources().getString(R.string.leftclick_pressure),
											p.getResources().getString(R.string.rightclick_key),
											p.getResources().getString(R.string.leftclick_timeout),
											p.getResources().getString(R.string.leftclick_tap),
											p.getResources().getString(R.string.leftclick_tap_or_timeout) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.leftclick_question);
			builder.setSingleChoiceItems(items, Globals.LeftClickMethod, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item)
				{
					dialog.dismiss();
					Globals.LeftClickMethod = item;
					if( item == Mouse.LEFT_CLICK_WITH_KEY )
						p.keyListener = new KeyRemapToolMouseClick(p, true);
					else if( item == Mouse.LEFT_CLICK_WITH_TIMEOUT || item == Mouse.LEFT_CLICK_WITH_TAP_OR_TIMEOUT )
						showLeftClickTimeoutConfig(p);
					else
						goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
		static void showLeftClickTimeoutConfig(final MainActivity p) {
			final CharSequence[] items = {	p.getResources().getString(R.string.leftclick_timeout_time_0),
											p.getResources().getString(R.string.leftclick_timeout_time_1),
											p.getResources().getString(R.string.leftclick_timeout_time_2),
											p.getResources().getString(R.string.leftclick_timeout_time_3),
											p.getResources().getString(R.string.leftclick_timeout_time_4) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.leftclick_timeout_time);
			builder.setSingleChoiceItems(items, Globals.LeftClickTimeout, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.LeftClickTimeout = item;
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class RightClickConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.rightclick_question);
		}
		boolean enabled()
		{
			return Globals.AppNeedsTwoButtonMouse;
		}
		void run (final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.rightclick_none),
											p.getResources().getString(R.string.rightclick_multitouch),
											p.getResources().getString(R.string.rightclick_pressure),
											p.getResources().getString(R.string.rightclick_key),
											p.getResources().getString(R.string.leftclick_timeout) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.rightclick_question);
			builder.setSingleChoiceItems(items, Globals.RightClickMethod, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.RightClickMethod = item;
					dialog.dismiss();
					if( item == Mouse.RIGHT_CLICK_WITH_KEY )
						p.keyListener = new KeyRemapToolMouseClick(p, false);
					else if( item == Mouse.RIGHT_CLICK_WITH_TIMEOUT )
						showRightClickTimeoutConfig(p);
					else
						goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}

		static void showRightClickTimeoutConfig(final MainActivity p) {
			final CharSequence[] items = {	p.getResources().getString(R.string.leftclick_timeout_time_0),
											p.getResources().getString(R.string.leftclick_timeout_time_1),
											p.getResources().getString(R.string.leftclick_timeout_time_2),
											p.getResources().getString(R.string.leftclick_timeout_time_3),
											p.getResources().getString(R.string.leftclick_timeout_time_4) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.leftclick_timeout_time);
			builder.setSingleChoiceItems(items, Globals.RightClickTimeout, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.RightClickTimeout = item;
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	public static class KeyRemapToolMouseClick implements MainActivity.KeyEventsListener
	{
		MainActivity p;
		boolean leftClick;
		public KeyRemapToolMouseClick(MainActivity _p, boolean leftClick)
		{
			p = _p;
			p.setText(p.getResources().getString(R.string.remap_hwkeys_press));
			this.leftClick = leftClick;
		}
		
		public void onKeyEvent(final int keyCode)
		{
			p.keyListener = null;
			int keyIndex = keyCode;
			if( keyIndex < 0 )
				keyIndex = 0;
			if( keyIndex > SDL_Keys.JAVA_KEYCODE_LAST )
				keyIndex = 0;

			if( leftClick )
				Globals.LeftClickKey = keyIndex;
			else
				Globals.RightClickKey = keyIndex;

			goBack(p);
		}
	}

	static class AdditionalMouseConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.pointandclick_question);
		}
		void run (final MainActivity p)
		{
			CharSequence[] items = {
				p.getResources().getString(R.string.pointandclick_joystickmouse),
				p.getResources().getString(R.string.click_with_dpadcenter),
				p.getResources().getString(R.string.pointandclick_relative)
			};

			boolean defaults[] = { 
				Globals.MoveMouseWithJoystick,
				Globals.ClickMouseWithDpad,
				Globals.RelativeMouseMovement
			};

			
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.pointandclick_question));
			builder.setMultiChoiceItems(items, defaults, new DialogInterface.OnMultiChoiceClickListener()
			{
				public void onClick(DialogInterface dialog, int item, boolean isChecked) 
				{
					if( item == 0 )
						Globals.MoveMouseWithJoystick = isChecked;
					if( item == 1 )
						Globals.ClickMouseWithDpad = isChecked;
					if( item == 2 )
						Globals.RelativeMouseMovement = isChecked;
				}
			});
			builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();
					if( Globals.RelativeMouseMovement )
						showRelativeMouseMovementConfig(p);
					else
						goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}

		static void showRelativeMouseMovementConfig(final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.accel_veryslow),
											p.getResources().getString(R.string.accel_slow),
											p.getResources().getString(R.string.accel_medium),
											p.getResources().getString(R.string.accel_fast),
											p.getResources().getString(R.string.accel_veryfast) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.pointandclick_relative_speed);
			builder.setSingleChoiceItems(items, Globals.RelativeMouseMovementSpeed, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.RelativeMouseMovementSpeed = item;

					dialog.dismiss();
					showRelativeMouseMovementConfig1(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}

		static void showRelativeMouseMovementConfig1(final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.none),
											p.getResources().getString(R.string.accel_slow),
											p.getResources().getString(R.string.accel_medium),
											p.getResources().getString(R.string.accel_fast) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.pointandclick_relative_accel);
			builder.setSingleChoiceItems(items, Globals.RelativeMouseMovementAccel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.RelativeMouseMovementAccel = item;

					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class JoystickMouseConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.pointandclick_joystickmousespeed);
		}
		boolean enabled()
		{
			return Globals.MoveMouseWithJoystick;
		};
		void run (final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.accel_slow),
											p.getResources().getString(R.string.accel_medium),
											p.getResources().getString(R.string.accel_fast) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.pointandclick_joystickmousespeed);
			builder.setSingleChoiceItems(items, Globals.MoveMouseWithJoystickSpeed, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.MoveMouseWithJoystickSpeed = item;

					dialog.dismiss();
					showJoystickMouseAccelConfig(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}

		static void showJoystickMouseAccelConfig(final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.none),
											p.getResources().getString(R.string.accel_slow),
											p.getResources().getString(R.string.accel_medium),
											p.getResources().getString(R.string.accel_fast) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.pointandclick_joystickmouseaccel);
			builder.setSingleChoiceItems(items, Globals.MoveMouseWithJoystickAccel, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.MoveMouseWithJoystickAccel = item;

					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class TouchPressureMeasurementTool extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.measurepressure);
		}
		boolean enabled()
		{
			return Globals.RightClickMethod == Mouse.RIGHT_CLICK_WITH_PRESSURE ||
					Globals.LeftClickMethod == Mouse.LEFT_CLICK_WITH_PRESSURE;
		};
		void run (final MainActivity p)
		{
			p.setText(p.getResources().getString(R.string.measurepressure_touchplease));
			p.touchListener = new TouchMeasurementTool(p);
		}

		public static class TouchMeasurementTool implements MainActivity.TouchEventsListener
		{
			MainActivity p;
			ArrayList<Integer> force = new ArrayList<Integer>();
			ArrayList<Integer> radius = new ArrayList<Integer>();
			static final int maxEventAmount = 100;
			
			public TouchMeasurementTool(MainActivity _p) 
			{
				p = _p;
			}

			public void onTouchEvent(final MotionEvent ev)
			{
				force.add(new Integer((int)(ev.getPressure() * 1000.0)));
				radius.add(new Integer((int)(ev.getSize() * 1000.0)));
				p.setText(p.getResources().getString(R.string.measurepressure_response, force.get(force.size()-1), radius.get(radius.size()-1)));
				try {
					Thread.sleep(10L);
				} catch (InterruptedException e) { }
				
				if( force.size() >= maxEventAmount )
				{
					p.touchListener = null;
					Globals.ClickScreenPressure = getAverageForce();
					Globals.ClickScreenTouchspotSize = getAverageRadius();
					Log.i("SDL", "SDL: measured average force " + Globals.ClickScreenPressure + " radius " + Globals.ClickScreenTouchspotSize);
					goBack(p);
				}
			}

			int getAverageForce()
			{
				int avg = 0;
				for(Integer f: force)
				{
					avg += f;
				}
				return avg / force.size();
			}
			int getAverageRadius()
			{
				int avg = 0;
				for(Integer r: radius)
				{
					avg += r;
				}
				return avg / radius.size();
			}
		}
	}
	
	static class CalibrateTouchscreenMenu extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.calibrate_touchscreen);
		}
		//boolean enabled() { return true; };
		void run (final MainActivity p)
		{
			p.setText(p.getResources().getString(R.string.calibrate_touchscreen_touch));
			Globals.TouchscreenCalibration[0] = 0;
			Globals.TouchscreenCalibration[1] = 0;
			Globals.TouchscreenCalibration[2] = 0;
			Globals.TouchscreenCalibration[3] = 0;
			ScreenEdgesCalibrationTool tool = new ScreenEdgesCalibrationTool(p);
			p.touchListener = tool;
			p.keyListener = tool;
		}

		static class ScreenEdgesCalibrationTool implements MainActivity.TouchEventsListener, MainActivity.KeyEventsListener
		{
			MainActivity p;
			ImageView img;
			Bitmap bmp;
			
			public ScreenEdgesCalibrationTool(MainActivity _p) 
			{
				p = _p;
				img = new ImageView(p);
				img.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
				img.setScaleType(ImageView.ScaleType.MATRIX);
				bmp = BitmapFactory.decodeResource( p.getResources(), R.drawable.calibrate );
				img.setImageBitmap(bmp);
				Matrix m = new Matrix();
				RectF src = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
				RectF dst = new RectF(Globals.TouchscreenCalibration[0], Globals.TouchscreenCalibration[1], 
										Globals.TouchscreenCalibration[2], Globals.TouchscreenCalibration[3]);
				m.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);
				img.setImageMatrix(m);
				p.getVideoLayout().addView(img);
			}

			public void onTouchEvent(final MotionEvent ev)
			{
				if( Globals.TouchscreenCalibration[0] == Globals.TouchscreenCalibration[1] &&
					Globals.TouchscreenCalibration[1] == Globals.TouchscreenCalibration[2] &&
					Globals.TouchscreenCalibration[2] == Globals.TouchscreenCalibration[3] )
				{
					Globals.TouchscreenCalibration[0] = (int)ev.getX();
					Globals.TouchscreenCalibration[1] = (int)ev.getY();
					Globals.TouchscreenCalibration[2] = (int)ev.getX();
					Globals.TouchscreenCalibration[3] = (int)ev.getY();
				}
				if( ev.getX() < Globals.TouchscreenCalibration[0] )
					Globals.TouchscreenCalibration[0] = (int)ev.getX();
				if( ev.getY() < Globals.TouchscreenCalibration[1] )
					Globals.TouchscreenCalibration[1] = (int)ev.getY();
				if( ev.getX() > Globals.TouchscreenCalibration[2] )
					Globals.TouchscreenCalibration[2] = (int)ev.getX();
				if( ev.getY() > Globals.TouchscreenCalibration[3] )
					Globals.TouchscreenCalibration[3] = (int)ev.getY();
				Matrix m = new Matrix();
				RectF src = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
				RectF dst = new RectF(Globals.TouchscreenCalibration[0], Globals.TouchscreenCalibration[1], 
										Globals.TouchscreenCalibration[2], Globals.TouchscreenCalibration[3]);
				m.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);
				img.setImageMatrix(m);
			}

			public void onKeyEvent(final int keyCode)
			{
				p.touchListener = null;
				p.keyListener = null;
				p.getVideoLayout().removeView(img);
				goBack(p);
			}
		}
	}
}

