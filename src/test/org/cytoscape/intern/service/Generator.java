package test.org.cytoscape.intern.service;

import java.io.InputStream;

/**
 * This interface provides for generating a CX file containing a single Cartesian
 * Layout fragment from a passed in CX file containing nodes
 * @author braxton
 *
 */
public interface Generator {
	public InputStream generateCartesianStream(String string, String algorithm);
}
