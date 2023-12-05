//
// Decompiled by Jadx - 900ms
//
package com.aide.ui.marketing;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.aide.ui.marketing.a;
import java.util.List;
import com.aide.ui.App;
import io.github.zeroaicy.aide.R;

@cy(clazz = -997865427726906315L, container = -997865427726906315L, user = true)
class WhatsNewDialog$PromotedAppListAdapter extends BaseAdapter {
    @fy
    private static boolean FH;
    @gy
    private static boolean Hw;
    @dy(field = -3095391488788896379L)
    private Context DW;
    @dy(field = -1868322362362947221L)
    private a.a j6;

    static {
        iy.Zo(WhatsNewDialog$PromotedAppListAdapter.class);
    }

    @ey(method = -84514163150598336L)
    public WhatsNewDialog$PromotedAppListAdapter(Context context) {
        try {
            if (FH) {
                iy.tp(186003988000272072L, (Object) null, context);
            }
            this.DW = context;
            for (a.a aVar : a.j6) {
                if (aVar.DW.equals(App.P8)) {
                    this.j6 = aVar;
                }
            }
        } catch (Throwable th) {
            if (Hw) {
                iy.j3(th, 186003988000272072L, (Object) null, context);
            }
        }
    }

    @Override
    @ey(method = -998587396990188468L)
    public int getCount() {
        try {
            if (FH) {
                iy.gn(203782999691271672L, this);
                return 2;
            }
            return 2;
        } catch (Throwable th) {
            if (Hw) {
                iy.aM(th, 203782999691271672L, this);
            }
        }
		return 2;
    }

    @Override
    @ey(method = -818842149512929085L)
    public Object getItem(int i) {
        try {
            if (FH) {
                iy.u7(-203887610944724343L, this, i);
            }
            return this.j6;
        } catch (Throwable th) {
            if (Hw) {
                iy.j3(th, -203887610944724343L, this, new Integer(i));
            }
            
        }
		return this.j6;
    }

    @Override
    @ey(method = 1027768060609764804L)
    public long getItemId(int i) {
        try {
            if (FH) {
                iy.u7(-284809481799784528L, this, i);
            }
            return i;
        } catch (Throwable th) {
            if (Hw) {
                iy.j3(th, -284809481799784528L, this, new Integer(i));
            }
        }
		return i;
    }

    @Override
    @ey(method = 2742632277161757635L)
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            if (FH) {
                iy.we(-2030958202756261779L, this, new Integer(i), view, viewGroup);
            }
            View inflate = view == null ? LayoutInflater.from(this.DW).inflate(0x7f0a0013, viewGroup, false) : view;
            if (i == 0) {
                TextView textView = inflate.findViewById(0x7f0801a4);
                textView.setText("What's new");
                textView.setVisibility(0);
                ImageView imageView = inflate.findViewById(0x7f0801a5);
                imageView.setImageResource(this.j6.j6);
                imageView.setVisibility(0);
                TextView textView2 = inflate.findViewById(0x7f0801a8);
                textView2.setText(this.j6.FH);
                textView2.setVisibility(0);
                TextView textView3 = inflate.findViewById(0x7f0801a6);
                textView3.setText(this.j6.Hw);
                textView3.setVisibility(8);
                TextView textView4 = inflate.findViewById(0x7f0801a7);
                List j6 = this.j6.j6(this.DW);
                String str = "New in " + WhatsNewDialog.j6(this.DW) + ":<br/>";
                for (int i2 = 0; i2 < j6.size(); i2++) {
                    String str2 = (String) j6.get(i2);
                    int indexOf = str2.indexOf(58);
                    if (indexOf > 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("<b>");
                        int i3 = indexOf + 1;
                        sb.append(str2.substring(0, i3));
                        sb.append("</b>");
                        sb.append(str2.substring(i3));
                        str2 = sb.toString();
                    }
                    str = str + str2;
                    if (i2 < j6.size() - 1) {
                        str = str + "<br/>";
                    }
                }
                textView4.setText(Html.fromHtml(str));
                textView4.setVisibility(0);
            } else {
                ((TextView) inflate.findViewById(R.id.whatsNewItemHeader)).setVisibility(8);
                ImageView imageView2 = inflate.findViewById(0x7f0801a5);
                imageView2.setImageResource(0x7f070003);
                imageView2.setVisibility(0);
                TextView textView5 = inflate.findViewById(0x7f0801a8);
                textView5.setText("Probelytics");
                textView5.setVisibility(0);
                TextView textView6 = inflate.findViewById(0x7f0801a6);
                textView6.setText("Realtime automatic analytics");
                textView6.setVisibility(0);
                ((TextView) inflate.findViewById(0x7f0801a7)).setVisibility(8);
            }
            return inflate;
        } catch (Throwable th) {
            if (Hw) {
                iy.U2(th, -2030958202756261779L, this, new Integer(i), view, viewGroup);
            }
			throw new Error(th);
        }
		
    }
}

