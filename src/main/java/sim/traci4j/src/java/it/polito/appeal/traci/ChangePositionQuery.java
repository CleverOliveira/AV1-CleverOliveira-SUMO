/*   
    Copyright (C) 2013 ApPeAL Group, Politecnico di Torino

    This file is part of TraCI4J.

    TraCI4J is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraCI4J is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraCI4J.  If not, see <http://www.gnu.org/licenses/>.
*/

package sim.traci4j.src.java.it.polito.appeal.traci;

import sim.traci4j.src.java.de.uniluebeck.itm.tcpip.Storage;
import sim.traci4j.src.java.it.polito.appeal.traci.protocol.Constants;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * This query allows to change the position of a POI.
 * @author Enrico Gueli &lt;enrico.gueli@polito.it&gt;
 */
public class ChangePositionQuery extends ChangeObjectVarQuery<Point2D> {
	ChangePositionQuery(DataInputStream dis, DataOutputStream dos,
			String objectID) {
		super(dis, dos, Constants.CMD_SET_POI_VARIABLE, objectID, Constants.VAR_POSITION);
	}

	/**
	 * After writing params, flushes the cache of {@link POI#changePositionQuery}.
	 */
	@Override
	protected void writeValueTo(Point2D position, Storage content) {
		content.writeByte(Constants.POSITION_2D);
		content.writeDouble(position.getX());
		content.writeDouble(position.getY());
	}
}