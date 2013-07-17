/**
 * eobjects.org MetaModel
 * Copyright (C) 2010 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.metamodel.data;

import java.io.Serializable;

/**
 * A {@link Style} represents the visual presentation ('styling') attributes of
 * a value in a {@link Row}. Styling can be used to highlight special values and
 * format the cells of eg. a spreadsheet.
 * 
 * Most datastores don't support styling, but some do. Those who do not support
 * it will just omit it.
 * 
 * Creation of {@link Style} objects is handled by the {@link StyleBuilder}
 * class.
 * 
 * @author Kasper Sørensen
 */
public interface Style extends Serializable {

	/**
	 * A style object used for values without styling, "unstyled" values or
	 * "neutrally styled" values.
	 */
	public static final Style NO_STYLE = new StyleImpl();

	/**
	 * Represents the text alignment of a value.
	 * 
	 * @author Kasper Sørensen
	 */
	public static enum TextAlignment {
		LEFT, RIGHT, CENTER, JUSTIFY
	}

	/**
	 * Represents a color used for value highlighting.
	 * 
	 * Creation of {@link Color} objects is handled by the static
	 * {@link StyleBuilder}.createColor(...) methods.
	 * 
	 * @author Kasper Sørensen
	 */
	public static interface Color extends Serializable {

		public short getRed();

		public short getGreen();

		public short getBlue();
	}

	/**
	 * Represents a unit of sizing elements (eg. fonts) in a {@link Style}.
	 * 
	 * @author Kasper Sørensen
	 */
	public static enum SizeUnit {
		/**
		 * Point unit
		 */
		PT,

		/**
		 * Pixel unit
		 */
		PX,

		/**
		 * Percent unit
		 */
		PERCENT
	}

	/**
	 * Determines whether or not the value is written in bold text.
	 * 
	 * @return true if text is bold
	 */
	public boolean isBold();

	/**
	 * Determines whether or not the value is written in italic text.
	 * 
	 * @return true if text is italic
	 */
	public boolean isItalic();

	/**
	 * Determines whether or not the value is written with an underline
	 * 
	 * @return true if text is underlined
	 */
	public boolean isUnderline();

	/**
	 * Gets the font size, or null if font size is unspecified.
	 * 
	 * @see SizeUnit
	 * 
	 * @return an Integer, or null
	 */
	public Integer getFontSize();

	/**
	 * Gets the unit of the font size.
	 * 
	 * @return an enum representing the font size unit used.
	 */
	public SizeUnit getFontSizeUnit();

	/**
	 * Gets the text alignment, or null if text alignment is unspecified.
	 * 
	 * @return a TextAlignment value, or null
	 */
	public TextAlignment getAlignment();

	/**
	 * Gets the foreground (text) color, or null if the color is unspecified.
	 * 
	 * @return a Color object representing the foreground color
	 */
	public Color getForegroundColor();

	/**
	 * Gets the background color, or null if the color is unspecified.
	 * 
	 * @return a Color object representing the background color
	 */
	public Color getBackgroundColor();

	/**
	 * Creates a Cascading Style Sheets (CSS) representation of this style.
	 * 
	 * @return a CSS string
	 */
	public String toCSS();
}