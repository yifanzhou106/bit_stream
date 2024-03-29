Project 5 - Bit Torrent Application
========================================================

### Overview

The architecture of the web application will be as follows:

**Tracker** - Tracker server will receive request and handle with all files and nodes information and will support APIs for the following operations:
1. Create a new seed information
2. Receive a new download request
3. Remove a invaild node 

**Client** - The client service will manage the list of seed file in pieces and an UI for user to input(Download or Upload). When a file is uploaded it is the responsibility of the Client to notify the Tracker infomation of files and its node detail. The API will support the following operations:
1. Receive file pieces and store into map
2. Send file pieces by file name and piece ID

### Architecture

**Tracker** - Tracker will have three map:
1. fileinfo (Map<String, HashMap<String, Integer>>) (filename, (piecenum, size))
   Use filename as key, get piecenum and size.
2. filemap (Map<String, HashMap<String, TreeSet<Integer>>>) (filename, (nodekey, (piecelist)))
   For each filename, it has a list of nodekey including its download-finished piece ID. 
3. nodeMap (Map<String, HashMap<String, String>>) (filemap, (host,port))
   Manage node host and port info.

**Client** - Client will have a map:
1. filemap (Map<String, TreeMap<Integer, byte[]>>) (filename, (pieceid, (byte [])))
   Store file in pieces.


### Logistics 

**Upload**: Load file from DIR, then divide them in to Byte array pieces(Each piece except the last one will have fixed size). After that, send file and its node information to the tracker. The tracker will store them into maps.

**Download**: New node will send post request to the tracker with its node information and file name. Tracker will then reply nodes and file detailed info. New node will create connection with seed node based on these info.

**Remove**: If seed node cannot be connected, receiver will tell the tracker this seed offline and need a new seed. Tracker will remove this seed info, and send new seed node to receiver.

### API

#### Tracker

<details>
<summary>POST /create</summary>

Body: 
<pre>
{
  node: {
	  "host": "string",
	  "port": "string"
        } 
  file: {
	  "filename": "string",
	  "piecenum": "string",
	  "size": "string"
	}
}
</pre>

Responses:

<table>
	<tr><td>Code</td></tr>
	<tr><td>200</td><<br/>
</td></tr>
	<tr><td>400</td><td>Create Error</td></tr>
</table>
</details>


<details>
<summary>POST /download </summary>
	
Body:

<pre>
{
  "host": "string",
  "port": "string",
  "filename": "string",
}
</pre>

Responses:

<table>
	<tr><td>Code</td><td>Description</td></tr>
	<tr><td>200</td><td>Node List
<pre>
{
  nodes: [
	   {
	     "host": "string",
	     "port": "string",
   	     "pieceid": "string"
	   }
	 ] 
  fileinfo: {
	      "piecenum": "string",
	      "size": "string"
            }
}	
</pre></td></tr>
	<tr><td>400</td><td>File request error</td></tr>
</table>
</details>

<details>
<summary>POST /remove</summary>

Body: 
<pre>
{
  "host": "string",
  "port": "string",
  "pieceid": "string",
  "filename": "string" 
}
</pre>

Responses:

<table>
	<tr><td>Code</td><td>Description</td></tr>
	<tr><td>200</td><td>Node List
<pre>

{
  "host": "string",
  "port": "string",
  "pieceid": "string",
  "filename": "string"
}
		   
	
</pre></td></tr>
	<tr><td>400</td><td>piece request error</td></tr>
</table>
</details>


#### Client

<details>
<summary>POST /receive</summary>

Body:

<pre>
{
  "host": "string",
  "port": "string",
  "pieceid": "string",
  "filename": "string"	   
}	
</pre>

Responses:

<table>
	<tr><td>Code</td><td>Description</td></tr>
	<tr><td>200</td><td>Event created
</td></tr>
	<tr><td>400</td><td>Piece unsuccessfully created</td></tr>

</table>
</details>


<details>
<summary>POST /send</summary>

Body:

<pre>
{
  "pieceid": "string",
  "filename": "string"	   
}	
</pre>

Responses:

<table>
	<tr><td>Code</td><td>Description</td></tr>
	<tr><td>200</td><td>Byte []
</td></tr>
	<tr><td>400</td><td>Piece unsuccessfully Send</td></tr>

</table>
</details>



### First Checking Point 
Messages can be download and print using BitTorrent-like approach for downloading text message.

### Final Goal
Program can send and download images, and more implements.


