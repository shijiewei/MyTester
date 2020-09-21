package com.example.weishj.mytester.crypto;

import com.mob.tools.utils.Data;

import java.security.MessageDigest;

public class MD5Test {
	public static final String[] BLACK_LIST =  {"5def89ccd2fe", "19796654ba2f0", "2e023c87d7383", "2abdab54e079", "15380ea9e6f64", "1577eae46b614", "8c002c5c30d5", "186da8a62552", "2887b839f48e8", "180262a7d3c90", "26f67a09bafd6", "222142b0d16fb", "2f1a7605c2eda", "299565c336f6e", "1ff10020bab60", "2a116acb0ec50", "2bb696ab38a41", "f6b049ecaa2c", "2fa0d87bf1765", "2abd574cd684", "28de25d7105b8", "1ef8311e429c0", "5df133447d37", "1a6535d15ac4f", "2e4a677537908", "14b6bfc60e500", "1922eb91362e", "62b7ced1f072", "267b84af0a3a7", "5e7d60627fb", "m2efd1ff916fb3", "23e1e9104c75c", "282f985dfec56", "ef6bf6123890", "6a1d85af5e52", "2b803f805aa12", "115f1f1b3fa8a", "2bcc0c557ca50", "1d30a96891654", "2e541450ad959", "302cebbfb05ae", "1ed811ef19498", "1963e16ad5198", "283a0c2171470", "1c731d6579c4a", "2480f930c66f4", "2a95312734b02", "a2f7a1e7e685", "186c5b350dca", "1963d3db69ca", "2c90457974424", "7cd60f5f653f", "2793821a688c8", "2e29f9b9d646c", "151cf582bad46", "2e4cb38c986cc", "563efaa5a8fe", "2ac214d0cd99c", "195bd0b311cb2", "1ce3b16f6b0a0", "bc6faeae6058", "14a86c16beb1a", "1eeab31aabb90", "94eb528bd424", "2c37bc262c84e", "1df975ef5c9a5", "2bbe6d4da4d58", "25672ddd11bd0", "2bd5578e8ccbc", "19441c52d40a0", "272a2b9189527", "30036a27d2a71", "62a6624d5661", "2cd128ae5c69e", "2f065c78d01b4", "2ba07ef2a05e0", "1da30a9ccf082", "2c268aed43e20", "26c6d9c19ca20", "1cd964d73419d", "16bc301b7f767", "16fee3149aacf", "24ac768fa3340", "2e6a9a0a3a562", "24ace5906bbf7", "9b5ca0d0518", "20b3fd7d21251", "21ae3a120ecc6", "1a06c023602b4", "232f162bb8250", "2a5ff9906a71b", "286824742d188", "8622feb1dec6", "18b9539333d26", "2ab3cf19526d1", "313ff2361d4c", "884fdc439afa", "6e3ee3d34943", "26241e7517b00", "1a887501893e0", "5947a53e79fe0", "4a00e180d6df", "449c902431fa", "859b081885ea", "2d254a1146dfe", "9f349133f4a2", "1cf8d63fc9791", "12333ed023b69", "17bfe963c5aa8", "2e04ed1f5b15c", "975e744cfa90", "1ff36caf273b8", "7e504f3328c8", "2929ca2b49f65", "162b9108b532c", "1967196a499c0", "23360e4b6b56c", "2d239cedd8190", "2c5976a5c95a0", "1c81ada37b39b", "27cd8be5bf648", "37432727b3bb8dbecf162b7791a83cd1", "2e67b5de1e48c", "41377fc83990", "17fe9d51b75b0", "1a161123e9741", "3ec249ec404e", "2ffa077f15466", "223e85d583b31", "d853d241fa6", "1cedb268b6d05", "45ef0337ec00", "1e9f34e4ce014", "14e2d9640971c", "2cca9356fbee0", "29956b93180da", "268df35b429a2", "2beb3a2546c8e", "284bbaf2cf4b4", "1fe1d863d3fbd", "2b4a3c05ec7a4", "21bd9980bd195", "e0c3e1646af5", "13ca71e63512a", "22621742410", "1ffcd8f8b06a0", "6bed9e6304bc", "12ef919605ee0", "2c0ab8e9bb714", "16f20616643c8", "1b8f2a33e317c", "26c41b5a5fe23", "2c998f2d9a727", "71c093771c10", "26cd61c7f915d", "1ba0d4f072b1f", "2b5ddaf87c1f4", "2376adf012ea4", "68454395f800", "2511043d8c682", "2e1d357a47a38", "1fbd3182a2516", "6997bb358380", "24e858e17f27c", "2af5c1ee7e97e", "3e5576f365c0", "14cbccd275470", "177506cc9f4a2", "90362837410c", "2bcd3b545d700", "9b95d0b6a70c", "589212f9cdc3", "2c570d9477234", "f66fccafd3c", "1ebb814bc07d7", "1cbcee71ff4c8", "295355a38eb9", "2728d90023890", "2d77186d8d6c8", "14e357c92b2b6", "2a8b6651d3ec0", "2823497f4cf51", "170f60d0bab90", "2b52076226e81", "1853de70f4285", "21b7c00ef7f83", "1f26c2dcea97e", "2ba20169d2bfd", "250556fd8ed29", "25a288809fcb9", "239485cab7316", "2e1c61dbe463c", "1c9167cbac73b", "10e22f093f255", "25089b50864f7", "c898c565b5e1", "2b1134e6aae8b", "29672910d16f4", "28cedb0feebd", "1f21a7160c6d2", "a94fad4a54", "17ebc5d6a6eb5", "282b565fb5038", "b1cbca5de52", "2a2a9f44a93c0", "47ff606dadb8", "62974b64abd8", "1e6d4de88d357", "2b4643c1ff800", "1316871abd6fd", "m2efef2cc8f048", "2ea2009654dfc", "251eed4ac6b67", "2e23b2388c808", "24589bf537fa0", "2e73ae196b142", "bb16fbef3996", "1b8a92bf98414", "2b150a6817d00", "2a9859e3789e0", "25b42ed4736b8", "cb9496223ea0", "228c117293586", "2fc0edd2c9688", "29274d5930c72", "2b524ba0384e4", "1b11f41650cc0", "280b3405c3ea8", "23835077229c0", "1ee233757d3c4", "262d85579cdce", "2e2d1d924d6f0", "12b1a4c845c0", "2f205679534e0", "a278731c26c", "16b012f234162", "7df44cc07e9a", "440306fac714", "2c019d5a38a60", "2bbd4cf94c0bb", "81e1bdefe02", "444477556143", "2695d39add749", "2e798e12596ce", "22ab1deb49f4f", "1fd23cd25727", "1f4444c14ea36", "4902e29849e6", "1c55975ab268f", "24289edf4d7c8", "19a9f775113d9", "22f8674521391", "1d52b255feff3", "aab34d24a8a", "664a1c993330", "2b2f0505e3322", "138f7cd8b20be", "efb47574e6ba", "2dbc98d67cd56", "152dd039ab7a0", "2aa0d8be0453d", "1f4169dee6c13", "2e2ce50a32898", "1f498543ade4c", "2a8231f75838c", "a2c83ab6aedc", "1e5e692757740", "2eb705a14c520", "2abca93eff578", "2816806c22a63", "15f1d0801079c", "50ef6fb11d48", "44b72f11a5ea", "1b4a22dbf98b4", "2af065ce9ad72", "247a6e7627619", "21bde4fa3e714", "2e818d83dffa2", "5af7187cb1a0", "2ee7f8d7433b4", "2c539b7ce5ca4", "214cb1ffc4ae0", "2c00ba3d643ed", "2882ae1b5ece8", "16ed208591fec", "d580ad56b4b5", "14a734d2f03d", "2480678bb10f5", "83a01ce61370", "2ae3e7efc36ed", "26393d60a3861", "16f4334172c20", "2884e9cf47196", "53836cde4fc8", "1ac26802079dc", "2e76d4292f1c3", "bcfc9e8f33ad", "2b7d55c49f106", "14d4df5329873", "16d14d810c97e", "2d58723a2a9e0", "m2eede6edde008", "267f38e3aa42c", "m3010eaf86c80e"};
	public static void main(String[] args) {
		for (String str : BLACK_LIST) {
			md5(str);
		}
		convert2Md5(BLACK_LIST);
	}

	private static void convert2Md5(String[] origin) {
		if (origin != null) {
			String rst = "";
			int size = origin.length;
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					rst += "[";
				}
				rst += "\"" + Data.byteToHex(Data.rawMD5(origin[i])) + "\"";
				if (i < size - 1) {
					rst += ", ";
				}
				if (i == size - 1) {
					rst += "]";
				}
			}
			System.out.println("convert to md5: " + rst);
		}
	}

	private static void md5(String origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(origin.getBytes());// 将original传给dumd5
			byte[] digist = md.digest();// 产生md5序列
			StringBuffer sb = new StringBuffer();// 转换zhimd5值为16进制dao
			for (byte b : digist) {
				sb.append(String.format("%02x", b & 0xff));
			}
			System.out.println("md5-1: " + sb);
			System.out.println("md5-2: " + Data.byteToHex(Data.rawMD5(origin)));
		} catch (Throwable t) {

		}
	}
}
