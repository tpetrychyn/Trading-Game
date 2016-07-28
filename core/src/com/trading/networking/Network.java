package com.trading.networking;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.JsonSerialization;

public class Network {
	  static public final int port = 54555;
	  public static final Integer PORT_TCP = 54555;
	  public static final Integer PORT_UDP = 54777;

	  static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		/*kryo.register(RegisterName.class);
		kryo.register(String[].class);
		kryo.register(UpdateNames.class);
		
		kryo.register(ServerMessage.class);*/
		
		/*kryo.register(Vector2.class);
		kryo.register(NpcMovePacket.class);
		kryo.register(NpcMovePacket[].class);
		
		
		kryo.register(PlayerMovePacket.class);
		
		kryo.register(Requests.class);
		kryo.register(ClientRequest.class);*/
		kryo.setRegistrationRequired(false);
	  }

	  static public class RegisterName {
		public String name;
	  }

	  static public class UpdateNames {
		public String[] names;
	  }

	  static public class ServerMessage {
		public String text;
	  }

}
