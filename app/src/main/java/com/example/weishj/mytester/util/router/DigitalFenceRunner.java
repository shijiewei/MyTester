package com.example.weishj.mytester.util.router;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by weishj on 2018/5/15.
 */

public interface DigitalFenceRunner {

//	public class RadioDevice implements Parcelable {
////		public static final Parcelable.Creator CREATOR = new m();
//		private HardwareAddress a;
//		private int b;
//		private boolean c;
//		private boolean d;
//		private HardwareAddress e;
//		private String f;
//		private int g;
//		private long h;
//		private long i;
////		private Node j;
//		private String k;
//		private long l;
//		private boolean m;
//		private boolean n;
//		private boolean o;
//		private HardwareAddress p;
//		private List q;
//
//		protected RadioDevice(Parcel paramParcel) {
//			this.a = ((HardwareAddress)paramParcel.readParcelable(HardwareAddress.class.getClassLoader()));
//			this.b = paramParcel.readInt();
//			if (paramParcel.readByte() != 0)
//			{
//				boolean bool1 = true;
//				this.c = bool1;
//				if (paramParcel.readByte() == 0) {
//					break label213;
//				}
//				bool1 = true;
//				label53:
//				this.d = bool1;
//				this.e = ((HardwareAddress)paramParcel.readParcelable(HardwareAddress.class.getClassLoader()));
//				this.f = paramParcel.readString();
//				this.g = paramParcel.readInt();
//				this.h = paramParcel.readLong();
//				this.i = paramParcel.readLong();
//				this.j = ((Node)paramParcel.readParcelable(Node.class.getClassLoader()));
//				this.k = paramParcel.readString();
//				this.l = paramParcel.readLong();
//				if (paramParcel.readByte() == 0) {
//					break label218;
//				}
//				bool1 = true;
//				label147:
//				this.m = bool1;
//				if (paramParcel.readByte() == 0) {
//					break label223;
//				}
//				bool1 = true;
//				label161:
//				this.n = bool1;
//				if (paramParcel.readByte() == 0) {
//					break label228;
//				}
//			}
//			label213:
//			label218:
//			label223:
//			label228:
//			for (boolean bool1 = bool2;; bool1 = false)
//			{
//				this.o = bool1;
//				this.p = ((HardwareAddress)paramParcel.readParcelable(HardwareAddress.class.getClassLoader()));
//				this.q = paramParcel.createTypedArrayList(DigitalFenceRunner.RadioDeviceTrack.CREATOR);
//				return;
//				bool1 = false;
//				break;
//				bool1 = false;
//				break label53;
//				bool1 = false;
//				break label147;
//				bool1 = false;
//				break label161;
//			}
//		}
//
//		public static final Creator<RadioDevice> CREATOR = new Creator<RadioDevice>() {
//			@Override
//			public RadioDevice createFromParcel(Parcel in) {
//				return new RadioDevice(in);
//			}
//
//			@Override
//			public RadioDevice[] newArray(int size) {
//				return new RadioDevice[size];
//			}
//		};
//
//		@Override
//		public int describeContents() {
//			return 0;
//		}
//
//		@Override
//		public void writeToParcel(Parcel paramParcel, int paramInt) {
//			byte b2 = 1;
//			paramParcel.writeParcelable(this.a, paramInt);
//			paramParcel.writeInt(this.b);
//			if (this.c)
//			{
//				b1 = 1;
//				paramParcel.writeByte(b1);
//				if (!this.d) {
//					break label180;
//				}
//				b1 = 1;
//				label43:
//				paramParcel.writeByte(b1);
//				paramParcel.writeParcelable(this.e, paramInt);
//				paramParcel.writeString(this.f);
//				paramParcel.writeInt(this.g);
//				paramParcel.writeLong(this.h);
//				paramParcel.writeLong(this.i);
//				paramParcel.writeParcelable(this.j, paramInt);
//				paramParcel.writeString(this.k);
//				paramParcel.writeLong(this.l);
//				if (!this.m) {
//					break label185;
//				}
//				b1 = 1;
//				label123:
//				paramParcel.writeByte(b1);
//				if (!this.n) {
//					break label190;
//				}
//				b1 = 1;
//				label137:
//				paramParcel.writeByte(b1);
//				if (!this.o) {
//					break label195;
//				}
//			}
//			label180:
//			label185:
//			label190:
//			label195:
//			for (byte b1 = b2;; b1 = 0)
//			{
//				paramParcel.writeByte(b1);
//				paramParcel.writeParcelable(this.p, paramInt);
//				paramParcel.writeTypedList(this.q);
//				return;
//				b1 = 0;
//				break;
//				b1 = 0;
//				break label43;
//				b1 = 0;
//				break label123;
//				b1 = 0;
//				break label137;
//			}
//		}
//	}
}
