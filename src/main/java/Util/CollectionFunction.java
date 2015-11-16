package Util;

import java.util.Collections;
import java.util.Vector;

public class CollectionFunction
{
	public static Vector<String> Compare2list(Vector<String> Ulist,
			Vector<String> Flist)
	{
		if (Ulist == null || Flist == null)
			return null;

		Vector<String> ret = new Vector<String>();

		Collections.sort(Ulist);
		Collections.sort(Flist);

		int ulen = Ulist.size();
		int flen = Flist.size();

		int up = 0;
		int fp = 0;

		while (up < ulen && fp < flen)
		{
			if (Ulist.get(up).equals(Flist.get(fp)))
			{
				ret.add(Ulist.get(up));
				up++;
				fp++;
			}
			else if (Ulist.get(up).compareTo(Flist.get(fp)) > 0)
			{
				fp++;
			}
			else
			{
				up++;
			}
		}

		return ret;
	}
}
