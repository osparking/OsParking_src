/*
 *  JLocaleChooser.java  - A bean for choosing locales
 *  Copyright (C) 2004 Kai Toedter
 *  kai@toedter.com
 *  www.toedter.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.toedter.components;

import static com.osparking.global.names.DB_Access.parkingLotLocale;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;

/*
The part changed by OsParking(www.osparking.com) is commented by OsParking in line.
Example: by OsParking on 2016. 6. 17.
*/

/**
 * JLocaleChooser is a bean for choosing locales.
 * 
 * @author Kai Toedter
 * @version $LastChangedRevision: 85 $
 * @version $LastChangedDate: 2006-04-28 13:50:52 +0200 (Fr, 28 Apr 2006) $
 */
public class JLocaleChooser extends JComboBox implements ItemListener {
	private static final long serialVersionUID = 8152430789764877431L;
	protected JComponent component;
        static Locale defaultLocale = new Locale("en", "US");        

	/**
	 * Default JLocaleChooser constructor.
	 */
	public JLocaleChooser(Locale defaultLocale) {
            this(null, defaultLocale);
	}

    /**
     * Returns "JLocaleChoose".
     *
     * @return the name value
     */
    public String getName() {
        return "JLocaleChoose";
    }

	/**
	 * Default JLocaleChooser constructor.
	 */
	public JLocaleChooser(JComponent component, Locale currLocale) {
		super();
		this.component = component;
		addItemListener(this);
                
                Locale locale1 = new Locale( // Added by OsParking on 2016. 6. 17.
                        currLocale.getLanguage(), 
                        currLocale.getCountry(), "WIN");
                Locale.setDefault(locale1); // upto here OsParking on 2016. 6. 17.
                
		locales = Calendar.getAvailableLocales();
                
                sortAlphaOrder(locales); // This line added by OsParking on 2016. 6. 17.
		localeCount = locales.length;

		for (int i = 0; i < localeCount; i++) {
                    if (locales[i].getCountry().length() > 0) {
                            addItem(locales[i].getDisplayName());
                    }
		}

		setLocale(Locale.getDefault());
	}

	/**
	 * The ItemListener for the locales.
	 */
	public void itemStateChanged(ItemEvent iEvt) {
		String item = (String) iEvt.getItem();
		int i;

		for (i = 0; i < localeCount; i++) {
			if (locales[i].getDisplayName().equals(item))
				break;
		}
		setLocale(locales[i], false);
	}

	/**
	 * Sets the locale.
	 * 
	 * @see #getLocale
	 */
	private void setLocale(Locale l, boolean select) {
		Locale oldLocale = locale;
		locale = l;
		int n = 0;

		if (select) {
			for (int i = 0; i < localeCount; i++) {
				if (locales[i].getCountry().length() > 0) {
					if (locales[i].equals(locale))
						setSelectedIndex(n);
					n += 1;
				}
			}
		}

		firePropertyChange("locale", oldLocale, locale);
		if(component != null) {
		    component.setLocale(l);
		}
	}

	/**
	 * Sets the locale. This is a bound property.
	 * 
	 * @see #getLocale
	 */
	public void setLocale(Locale l) {
		setLocale(l, true);
	}

	/**
	 * Returns the locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Creates a JFrame with a JLocaleChooser inside and can be used for
	 * testing.
	 */
	static public void main(String[] s) {
		JFrame frame = new JFrame("LocaleChooser");
		frame.getContentPane().add(new JLocaleChooser(parkingLotLocale));
		frame.pack();
		frame.setVisible(true);
	}

	private Locale[] locales;
	private Locale locale;
	private int localeCount;

    /**
     * Sort locales on its display name for user friendliness.
     * 
     * This module is added by OsParking on 2016. 6. 17.
     * @param locales array of locales that are not sorted by the display name.
     */
    private void sortAlphaOrder(Locale[] locales) {
        // first convert array to list
        List<Locale> caList = Arrays.asList(locales);
        Collections.sort(caList, new Comparator<Locale>() {
            @Override
            public int compare(Locale o1, Locale o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });
        
        caList.toArray(locales);
    }
}

