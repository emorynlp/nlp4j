/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.common.util;

import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.collection.tree.CharAffixTree;
import edu.emory.mathcs.nlp.common.constant.CharConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MetaUtils
{
	private MetaUtils() {}
	
	static public final String[] PROTOCOLS = {"http://","https://","https://","ftp://","sftp://"};
	static public final Pattern EMOTICON = Pattern.compile("[\\!\\|;:#%][-]*[\\(\\)\\[\\]\\{\\}\\|<>]+");
	static public final CharAffixTree FILE_EXTENSION = new CharAffixTree(false, new String[]{"3gp","7z","ace","ai","aif","aiff","amr","asf","asp","aspx","asx","avi","bat","bin","bmp","bup","cab","cbr","cda","cdl","cdr","chm","dat","divx","dll","dmg","doc","dss","dvf","dwg","eml","eps","exe","fla","flv","gif","gz","hqx","htm","html","shtml","ifo","indd","iso","jar","jsp","jpg","jpeg","lnk","log","m4a","m4b","m4p","m4v","mcd","mdb","mid","mov","mp2","mp3","mp4","mpg","mpeg","msi","mswmm","ogg","pdf","php","png","pps","ppt","ps","psd","pst","ptb","pub","qbb","qbw","qxd","ram","rar","rm","rmvb","rtf","sea","ses","sit","sitx","sql","ss","swf","tgz","tif","torrent","ttf","txt","vcd","vob","wav","wma","wmv","wpd","wps","xls","xml","xtm","zip"});
	
	static public final Pattern HYPERLINK = Pattern.compile(
			// protocol (http, https, ftp)
			"(\\p{Alpha}{3,9}://)?" +
			// id:pass (id:pass@, id:@, id@, mailto:id@)
			"([\\p{Alnum}_]+(:\\S*)?@)?" +
		"(" +
			// IPv4 address (255.248.27.1)
			"(" + "\\d{3}" + "(\\.\\d{1,3}){3}" + ")" +
		"|" +
			// host + domain + TLD name (www.clearnlp.com, www-01.clearnlp.com, mathcs.emory.edu, clearnlp.co.kr)
			"(" + "\\w+(-\\w+)*" + "(\\.\\w+(-\\w+)*)*" + "\\.\\p{Alpha}{2,}" + ")" +
		")" +
			// port number
			"(:\\d{2,5})?" +
			// resource path
			"(/\\S*)?");

	
	static public boolean startsWithNetworkProtocol(String s)
	{
		s = StringUtils.toLowerCase(s);
		
		for (String protocol : PROTOCOLS)
		{
			if (s.startsWith(protocol))
				return true;
		}
		
		return false;
	}
	
	static public boolean containsHyperlink(String s)
	{
		return startsWithNetworkProtocol(s) || HYPERLINK.matcher(s).find();
	}
	
	static public boolean endsWithFileExtension(String s)
	{
		int idx = FILE_EXTENSION.getAffixIndex(s, false);
		return (idx > 0) ? s.charAt(idx-1) == CharConst.PERIOD : false;
	}
}
