package test.org.cytoscape.intern.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
impo

public class LayoutServiceTest {

	@Test
	public void test() {
		fail("Not yet implemented");
		InputStream cxInput = ClassLoader.getSystemResourceAsStream("resources/cxInput");
		NodesFragmentReader nodesFragmentReader = NodesFragmentReader.createInstance();
		HashSet<AspectFragmentReader> hashReader = new HashSet<AspectFragmentReader>();
		hashReader.add(nodesFragmentReader);
		CxReader cxReader = CxReader.createInstance(cxInput, hashReader);
		
		ArrayList<NodesElement> nodes = new ArrayList<NodesElement>();
		while (cxReader.hasNext()) {
			List<AspectElement> aspectElements = cxReader.getNext();
			for (AspectElement element : aspectElements) {
				if (element.getAspectName().equals(NodesElement.ASPECT_NAME)) {
					nodes.add((NodesElement)element);
				}
			}
		}
		Generator directGenerator = new DirectGenerator();
		InputStream cartesianResult= directGenerator.generateCartesianStream(cxInput, "grid");
		CarteisanFragmentReader cartesianFragmentReader = CartesianFragmentReader.createInstance();
		HashSet<AspectFragmentReader> cartReader = new HashSet<AspectFragmentReader>();
		hashReader.add(cartesianFragmentReader);
		CxReader cxReader = CxReader.createInstance(cxInput, cartReader);
		
		ArrayList<CartesianLayoutElement> layout = new ArrayList<CartesianLayoutElement>();
		while (cxReader.hasNext()) {
			List<AspectElement> cartElements = cxReader.getNext();
			for (AspectElement element : cartElements) {
				if (element.getAspectName().equals(CartesianLayoutElement.ASPECT_NAME)) {
					layout.add((CartesianLayoutElement)element);
				}
			}
		}
		
		assertEquals(nodes.size()==layout.size());
		
		
	}

}
