/*Copyright (c) 2018-2019, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package mcs.graphics;

import mcs.utils.StringUtils;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class SVG {

	SVGGraphics2D m_graphics = null;

	public SVG(int width, int height, Color background) {
		DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
		SVGDocument document = (SVGDocument) domImpl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
		m_graphics = new SVGGraphics2D(document);
		m_graphics.setSVGCanvasSize(new Dimension(width, height));
		m_graphics.setColor(background);
		m_graphics.fillRect(0, 0, width, height);
	}

	public SVG(File svgFile) throws IOException {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		SVGDocument document = (SVGDocument) factory.createDocument(svgFile.toURI().toString());
		m_graphics = new SVGGraphics2D(document);
	}

	public void drawRect(int x, int y, int width, int height, Color borderColor, Color fillColor) {
		drawRect(x, y, width, height, borderColor, fillColor, null);
	}

	public void fillRect(int x, int y, int width, int height, Color fillColor) {
		m_graphics.setColor(fillColor);
		m_graphics.fillRect(x, y, width, height);
	}

	public void drawRect(int x, int y, int width, int height, Color borderColor, Color fillColor, Integer thickness) {
		Stroke oldStroke = null;

		if(thickness != null) {
			oldStroke = m_graphics.getStroke();
			m_graphics.setStroke(new BasicStroke(thickness));
		}

		if(borderColor != null) {
			m_graphics.setColor(borderColor);
		}
		m_graphics.drawRect(x, y, width, height);
		if(fillColor != null) {
			m_graphics.setColor(fillColor);
			m_graphics.fillRect(x, y, width, height);
		}

		if(thickness != null) {
			m_graphics.setStroke(oldStroke);
		}
	}

	public void drawLine(int x1, int y1, int x2, int y2, Color color) {
		if(color != null) {
			m_graphics.setColor(color);
		}
		m_graphics.drawLine(x1, y1, x2, y2);
	}

	public void drawString(int x, int y, String text, String fontName, Color color) {
		if(fontName != null) {
			m_graphics.setFont(Font.decode(fontName));
		}
		if(color != null) {
			m_graphics.setColor(color);
		} else {
			m_graphics.setColor(Color.BLACK);
		}
		m_graphics.drawString(text, x, y);
	}

	public void export(File file) throws IOException {
		boolean useCSS = true;
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file), StringUtils.UTF8);
			m_graphics.stream(out, useCSS);
		} catch(Exception e) {
			throw new IOException(e);
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}
	}

	public static Color randomColor() {
		return new Color(randomInt(0, 255), randomInt(0, 255), randomInt(0, 255));
	}

	public static int randomInt(int min, int max) {
		double r = Math.random();
		return min + (int) (r * (max - min));
	}

	public static void main(String[] args) throws Exception {
		SVG svg = new SVG(500, 500, Color.WHITE);
		svg.drawRect(10, 10, 300, 150, Color.BLACK, Color.BLUE);
		svg.export(new File("test.svg"));

		//		SVG svg = new SVG(new File("svg/C-major_a-minor.svg"));
		SVG _svg = new SVG(new File("test.svg"));
		_svg.export(new File("export.svg"));
	}

}
