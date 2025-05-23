from scapy.all import * # given 
from scapy.layers.http import HTTPRequest # import HTTP packet
from scapy.layers.tls.all import TLS # import TLS class for processing raw bytes
import cryptography # it was a hint, so just put it here
load_layer("tls") # load tls layer
load_layer("http") # load HTTP layer
import sys # for command line arguments
import os.path # to check for tracefile
import datetime # for timestamps

# ----------------------------------------------------------------

def getIdx(array, element, other):
	for i in range(0, len(array)):
		if(array[i] == element):
			if(array[i] == other or i + 1 >= len(array)):
				return -2
			else:
				return i + 1
	
	return -1

# ----------------------------------------------------------------

def outputDNS(packet, currTime):
	src = packet[IP].src
	sport = str(packet[UDP].sport)
	
	dst = packet[IP].dst
	dport = str(packet[UDP].dport)
	
	qname = packet[DNS]["DNS Question Record"].qname.decode("utf-8")
	
	msg = currTime + " DNS " + src + ":" + sport + " --> " + dst + ":" + dport + " " + qname
	return msg
	
# ----------------------------------------------------------------
	
def outputHTTP(packet, currTime):
	src = packet[IP].src
	sport = str(packet[TCP].sport)
	
	dst = packet[IP].dst
	dport = str(packet[TCP].dport)
	
	host = packet[HTTPRequest].Host.decode("utf-8")
	method = packet[HTTPRequest].Method.decode("utf-8")
	path = packet[HTTPRequest].Path.decode("utf-8")
	
	msg = currTime + " HTTP " + src + ":" + sport + " --> " + dst + ":" + dport + " " + host + " " + method + " " + path
	
	#print("TIME: " + str(packet.time))
	return msg

# ----------------------------------------------------------------	
	
def outputTLSCH(packet, currTime):	
	src = packet[IP].src
	sport = str(packet[TCP].sport)
	
	dst = packet[IP].dst
	dport = str(packet[TCP].dport)

	sni = ""
	try:
		sni = packet[TLSClientHello]["TLS Extension - Server Name"].servernames[0].servername.decode("utf-8")
	except Exception:
		sni = ""
	
	msg = currTime + " TLS " + src + ":" + sport + " --> " + dst + ":" + dport + " " + sni
	return msg

# ----------------------------------------------------------------	

def rawHTTPPacket(packet, currTime):
	load = packet[Raw].load.decode("utf-8")
	words = load.split()
	method = words[0] # first word is method
	uri = words[1] # second word is request uri
	
	host = ""
	for i in range(3, len(words)):
		if(words[i] == "Host:"):
			host = words[i+1]
			break
	
	src = packet[IP].src
	sport = str(packet[TCP].sport)
	
	dst = packet[IP].dst
	dport = str(packet[TCP].dport)
	msg = currTime + " HTTP " + src + ":" + sport + " --> " + dst + ":" + dport + " " + host + " " + method + " " + uri
	print(msg)

# ----------------------------------------------------------------
	
def rawTLSClientHello(packet, currTime):
	#packet.show()
	tls_packet = TLS(packet[Raw].load)
	src = packet[IP].src
	dst = packet[IP].dst
	sport = str(packet[TCP].sport)
	dport = str(packet[TCP].dport)
	sni = ""
	try:
		sni = tls_packet[TLSClientHello]["TLS Extension - Server Name"].servernames[0].servername.decode("utf-8")
	except Exception:
		sni = ""
	
	msg = currTime + " TLS " + src + ":" + sport + " --> " + dst + ":" + dport + " " + sni
	print(msg)
	
# ----------------------------------------------------------------
	
def rawPacket(packet, currTime):
	load = packet[Raw].load
	#packet.show()
	try:
		if(load.find(b"GET /") != -1 or load.find(b"POST /") != -1):
			# raw HTTP Packet (non-standard port)
			rawHTTPPacket(packet, currTime)
		elif(len(load) > 5 and load[0] == 0x16 and load[5] == 0x01):
			# raw TLS Client Hello Packet (non-standard port)
			rawTLSClientHello(packet, currTime)
	except Exception:
		try:
			if(b"TLS Extension - Server Name" in load):
				print(load.decode("utf-8"))
		except Exception:
			return		

# ----------------------------------------------------------------

def packetHandler(packet):	
	pktTime = float(packet.time)
	currTime = datetime.datetime.fromtimestamp(pktTime).strftime('%Y-%m-%d %H:%M:%S.%f')
	
	if(DNS in packet):
		try:
			packet[DNS]["DNS Resource Record"] # if it exists, do nothing
		except:
			if(packet[DNS]["DNS Question Record"].qtype == 1):
				msg = outputDNS(packet, currTime)
				print(msg)
	elif(packet.haslayer(HTTPRequest) and packet.haslayer(IP)):
		if(str(packet[HTTPRequest].Method) == 'GET' or str(packet[HTTPRequest].Method == 'POST')):
			msg = outputHTTP(packet, currTime)
			print(msg)
	elif(TLSClientHello in packet):
		msg = outputTLSCH(packet, currTime)
		print(msg)
	elif(packet.haslayer(Raw) and packet.haslayer(TCP)): #Both http and tlsclient hello on tcp
		rawPacket(packet, currTime)

# ----------------------------------------------------------------
	
def main(argv):
	cnt = 1
	
	lidx = getIdx(argv, "-i", "-r")
	interface = argv[lidx] if lidx >= 0 else conf.iface
	if(lidx == -2):
		print("Invalid Usage. Use '-h help' option to learn about command line arguments with capture.py")
		return -1
	elif(lidx != -1):
		cnt = cnt + 2
		
	ridx = getIdx(argv, "-r", "-i")
	tracefile = argv[ridx].strip() if ridx >= 0 else ""	
	if(ridx == -2):
		print("Invalid Usage. Use '-h help' option to learn about command line arguments with capture.py")
		return -1
	elif(ridx != -1):
		cnt = cnt + 2
		
	expression = argv[cnt:] if cnt < len(argv) else ""
	expression = " ".join(expression)
	
	if(tracefile == ""):
		try:
			sniff(iface=interface, filter=expression, prn = packetHandler, store = 0)
		except Exception as e:
			print("Exception")
			print(e)
			return -1
	else:
		if(os.path.isfile(tracefile)): # ensure file exists
			try:
				sniff(offline=tracefile, prn=packetHandler, filter=expression, store=0)
			except Exception as e:
				print("Exception")
				print(e)
				return -1
		else:
			print("Provided tracefile does not exist. Use '-h help' option to learn more about usage.")
			return -1
			
# ----------------------------------------------------------------
	
main(sys.argv)
