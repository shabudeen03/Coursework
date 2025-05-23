import sys # For command line arguments
import re # For regular expressions
from scapy.all import IP, TCP, sr1, conf # For crafting syn packets
import socket # For TCP Connections
import ssl # For TLS Connections
import string # For printable output of a response data

# commonly used TCP ports to scan on if '-p' is not provided
standard_ports = [21, 22, 23, 25, 80, 110, 143, 443, 587, 853, 993, 3389, 8080]

def parseUserInputs(argv, standard_ports):
	target = ""
	scanning_ports = []
	if(len(argv) == 2 or len(argv) == 4):
		if(len(argv) == 4 and argv[1] != '-p'):
			print("No '-p' option detected")
			return -3
			
		if(len(argv) == 4):
			if(argv[2].find("-") >= 0): # Port Range
				ranges = argv[2].split("-")
				scanning_ports = list(range(int(ranges[0]), int(ranges[1]) + 1))[:]
			else: # Single Port
				scanning_ports.append(int(argv[2]))
		else: # No Ports Specified
			scanning_ports = standard_ports.copy() # no '-p' option provided
			
		if(re.search("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+", argv[-1])): # check for valid ip address format between 0.0.0.0 - 255.255.255.255
			nums = argv[-1].split(".")
			for num in nums:
				if(int(num) < 0 or int(num) > 255):
					print("Invalid IP address provided.")
					return -4

			target = argv[-1]
		else:
			print("Target IP Address not valid.") 
			return -2
	else:
		print("Invalid command line usage.")
		return -1 # invalid number of arguments
	
	print("Target: " + target)
	print("Scanning Ports: ", scanning_ports)
	return target, scanning_ports
	
def findOpenPorts(target, scanning_ports):
	# This increases timeout to wait for responses
	conf.verb = 0 # Suppresses Scapy Output

	open_ports = []
	for port in scanning_ports:
		# Create syn packet with syn flag
		syn_packet = IP(dst=target) / TCP(dport=port, flags="S")
		response = sr1(syn_packet, timeout=1, verbose=False)
		
		
		# If response is defined, check if it is a SYNACK
		if response:
			# response.show()
			if response.haslayer(TCP) and (response[TCP].flags == 'A' or response[TCP].flags == 'SA'):
				open_ports.append(port)
			
	print("Open Ports: ", open_ports)
	return open_ports
	
def clean_response(response):
	return ''.join(c if c in string.printable and c not in '\r\n' else '.' for c in response)
	
def is_tcp_banner(target, port):
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.settimeout(3)
		s.connect((target, port))
		try:
			banner = s.recv(1024).decode('utf-8', errors='ignore')
			if banner:
				banner = clean_response(banner)
				print("Type: (1) TCP server-initiated\nResponse: ", banner)
				return True
			else:
				return False
		except Exception as e:
		#	print("Exception: ", e)
			s.close()
			return False
	except Exception as e:
		# print("ABC")
		return False
		
def is_tls_banner(target, port):		
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		context = ssl.create_default_context(ssl.Purpose.SERVER_AUTH) 
		context.check_hostname = False # Don't check host header
		context.verify_mode = ssl.CERT_NONE # Don't check self signed certificates
		
		s.settimeout(3) # Timeout after 3 seconds
		s.connect((target, port))
		
		tls_socket = context.wrap_socket(s, server_hostname=target)
		try:
			banner = tls_socket.recv(1024).decode('utf-8', errors='ignore')
			tls_socket.close()
			banner = clean_response(banner)
			if banner:
				print("Type: (2) TLS server-initiated\nResponse: ", banner)
				return True
			else:
				return False
		except Exception as e:
			tls_socket.close()
			return False
	except Exception as e:
		# print(e)
		return False
			
def is_http(target, port):
	try:
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			s.settimeout(3) # Timeout after 3 seconds
			s.connect((target, port))
			s.sendall(b"GET / HTTP/1.0\r\n\r\n")
			response = s.recv(1024).decode('utf-8', errors='ignore')
			response = clean_response(response)
			if response.startswith("HTTP"):
				print("Type: (3) HTTP server\nResponse: ", response)
				return True
			else:
				return False
	except Exception as e:
		return False
		
def is_https(target, port):
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		context = ssl.create_default_context(ssl.Purpose.SERVER_AUTH) 
		context.check_hostname = False # Don't check host header
		context.verify_mode = ssl.CERT_NONE # Don't check self signed certificates
		
		s.settimeout(3) # Timeout after 3 seconds
		# print("A")
		s.connect((target, port))
		# print("B")
		tls_socket = context.wrap_socket(s, server_hostname=target)
		tls_socket.sendall(b"GET / HTTP/1.0\r\n\r\n")
		response = tls_socket.recv(1024).decode('utf-8', errors='ignore')
		tls_socket.close()
		response = clean_response(response)
		if response.startswith("HTTP"):
			print("Type: (4) HTTPS server\nResponse: ", response)
			return True
		else:
			return False
	except Exception as e:
		# print(e)
		return False

def is_generic_tls(target, port):
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		context = ssl.create_default_context(ssl.Purpose.SERVER_AUTH)
		context.check_hostname= False
		context.verify_mode = ssl.CERT_NONE
		s.settimeout(3)
		
		s.connect((target, port))
		tls_socket = context.wrap_socket(s, server_hostname=target)
		tls_socket.sendall(b"\r\n\r\n\r\n\r\n")
		response = tls_socket.recv(1024).decode('utf-8', errors='ignore')
		tls_socket.close()
		response = clean_response(response)
		if response:
			print("Type: (6) Generic TLS server\nResponse: ", response)
			return True
		else:
			return False
	except Exception as e:
		return False

def is_generic_tcp(target, port):
	print("Type: (5) Generic TCP server")
	try:
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.settimeout(3)
		s.connect((target, port))
		s.sendall(b"\r\n\r\n\r\n\r\n")
		response = s.recv(1024).decode('utf-8', errors='ignore')
		response = clean_response(response)
		if response:
			print("Response: ", response)
			return True
		else:
			return False
	except Exception as e:
		print("Response: ", e)
		return False

def main(argv):	
	# Retrieve Target ip address + port(s) to scan on
	target, scanning_ports = parseUserInputs(argv, standard_ports)
	
	# Perform Syn-Scanning and return list of open ports
	open_ports = findOpenPorts(target, scanning_ports) # Perform SYN-Scanning for open ports
	
	if len(open_ports) == 0:
		print("No Open Ports Detected")
		return 0
	
	print("\nScanning Ports now ...\n-----------------------------------------\n")

	
	# Fingerprint Service - probe in the following sequence
	# 	Each probe outputs the "type" + "response" after the "host" output from before
	for port in open_ports:
		print("Host: " + target + ":" + str(port))
		if(is_tls_banner(target, port)):
			continue
		elif(is_https(target, port)):
			continue
		elif(is_generic_tls(target, port)):
			continue
		elif(is_tcp_banner(target, port)):
			continue
		elif(is_http(target, port)):
			continue
		else:
			is_generic_tcp(target, port)

main(sys.argv)
