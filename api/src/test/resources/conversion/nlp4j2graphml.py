#!/usr/bin/python
# Optimized for yEd 3.7.0.2
# Author: Jinho D. Choi (choijd@colorado.edu)
import Tkinter
import tkFont
import operator
import sys

PREFIX = '\n'.join([\
'<?xml version="1.0" encoding="UTF-8" standalone="no"?>',\
'<graphml xmlns="http://graphml.graphdrawing.org/xmlns" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:y="http://www.yworks.com/xml/graphml" xmlns:yed="http://www.yworks.com/xml/yed/3" xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd">',\
'<key for="node" id="d0" yfiles.type="nodegraphics"/>',\
'<key for="edge" id="d1" yfiles.type="edgegraphics"/>',\
'<key attr.name="Description" attr.type="string" for="graph" id="d2">',\
'<default/>',\
'</key>',\
'<graph edgedefault="directed" id="G">\n'])
SUFFIX = '\n'.join(['</graph>','</graphml>\n'])
	
FONT_TOKEN_FAMILY = 'Times New Roman'
FONT_EDGE_FAMILY  = 'Arial'
FONT_TOKEN_SIZE   = 13 #*2
FONT_TAG_SIZE     = 12
FONT_INDEX_SIZE   = 10 #*2
FONT_EDGE_SIZE    = 10 #*2

B_SMOOTH = 'false'
B_POS    = False

H_GAP  = 25
V_GAP  = 20
E_GAP  = 6
X_INIT = 10
Y_INIT = 10

if sys.platform == 'darwin': RATE_TOKEN = 1.0
else                       : RATE_TOKEN = 0.9

class DepNode:
	def __init__(self, l):
		self.id      = int(l[0])
		self.form    = l[1]
		self.lemma   = l[2]
		self.pos     = l[3]
		self.feats   = l[4]
		self.headId  = int(l[5])
		self.label   = l[6]
		self.connect = list()
		self.vgap    = 0

class CoNLL2Graphml:
	def __init__(self, args):
		self.initArgs(args)
		global Y_INIT
		
		root    = Tkinter.Tk()
		tFont   = tkFont.Font(family=FONT_TOKEN_FAMILY, size=FONT_TOKEN_SIZE)
		sFont   = tkFont.Font(family=FONT_TOKEN_FAMILY, size=FONT_INDEX_SIZE)
		eFont   = tkFont.Font(family=FONT_EDGE_FAMILY , size=FONT_EDGE_SIZE)
		tHeight = tFont.metrics('linespace')
		sHeight = sFont.metrics('linespace')
		
		self.f_out.write(PREFIX)
		self.BEGIN_ID  = 0
		(dNode, lEdge) = self.getData()
		
		while dNode:
			self.setNodes(dNode, tFont, sFont, tHeight, sHeight)
			self.setEdges(dNode, lEdge, eFont)
			
			self.printNodes(dNode)
			self.printEdges(dNode)
			
			self.BEGIN_ID += len(dNode) + 1
			(dNode, lEdge) = self.getData()
			Y_INIT += 100
		
		root.destroy()
		self.f_out.write(SUFFIX)
		self.f_out.close()
		self.f_in.close()

####################### Initialize Arguments #######################

	def initArgs(self, args):
		if len(args) < 3: self.printUsage(args[0])
		global H_GAP, V_GAP, B_SMOOTH, B_POS
		
		inputFile  = None
		outputFile = None
		
		size = len(args)
		i = 1
		while i < size:
			key = args[i]

			if   key == '-i' and i+1 < size: inputFile  = args[i+1]; i += 2
			elif key == '-o' and i+1 < size: outputFile = args[i+1]; i += 2
			elif key == '-h' and i+1 < size: H_GAP      = int(args[i+1]); i += 2
			elif key == '-v' and i+1 < size: V_GAP      = int(args[i+1]); i += 2
			elif key == '-s'               : B_SMOOTH   = 'true'; i += 1
			elif key == '-p'               : B_POS      = True  ; i += 1
			else                           : self.printUsage(args[0])
		
		if inputFile: self.f_in = open(inputFile)
		else        : self.printUsage(args[0])
		
		if outputFile: self.f_out = open(outputFile, 'w')
		else         : self.f_out = open(inputFile+'.graphml', 'w')
		
	def printUsage(self, cmd):
		print 'Usage: %s -i <input_file> [-o <output_file> -h <horizontal_gap> -v <vertical_gap> -p -s]' % (cmd)
		print '- input_file     (required): dependency trees in text format'
		print '- output_file    (optional): dependency trees in Graphml format (default = \'input_file.graphml\')'
		print '- horizontal_gap (optional): gaps between tokens (default = 25)'
		print '- vertical_gap   (optional): gaps between edges (default = 18)'
		print '-p: if set, include part-of-speech tags (values in the 3rd column)'
		print '-s: if set, draw smooth edges instead of squared edges'

		sys.exit(0)	

################## Get Data ##################

	def getData(self):
		global Y_INIT
		
		lNode = list()
		for line in self.f_in:
			l = line.split()
			if not l: break
			lNode.append(DepNode(l))

		if not lNode: return (None, None)

		dNode = dict()
		for node in lNode:
			node.id     += self.BEGIN_ID
			node.headId += self.BEGIN_ID
			dNode[node.id] = node
		
		lRoot = list()
		sDiff = set()
		for currId in dNode:
			curr = dNode[currId]
			curr.connect.append(curr.headId)

			if curr.headId in dNode:
				dNode[curr.headId].connect.append(currId)
			else:
				lRoot.append(currId)
						
			sDiff.add(abs(currId - curr.headId))

		maxVgap = 0
		for diff in sorted(sDiff):
			for currId in dNode:
				curr = dNode[currId]
				if abs(currId - curr.headId) != diff: continue
				
				if currId < curr.headId:
					bId = currId
					eId = curr.headId
				else:
					bId = curr.headId + 1
					eId = currId
	
				m = 0
				for id in range(bId, eId): m = max(m, dNode[id].vgap)
				curr.vgap = m + 1
				maxVgap = max(curr.vgap, maxVgap)
				curr.connect.sort()

		Y_INIT += V_GAP * maxVgap

		lEdge = list()
		lRoot.sort()
		for currId in dNode:
			curr = dNode[currId]
			(cRank, cTotal) = self.getRank(curr.connect, currId, curr.headId)
			
			if curr.headId in dNode:
				(hRank, hTotal) = self.getRank(dNode[curr.headId].connect, curr.headId, currId)
			else:
				(hRank, hTotal) = self.getRank(lRoot, curr.headId, currId)
			
			t = [currId, curr.headId, curr.label, cRank, cTotal, hRank, hTotal, curr.vgap]
			lEdge.append(t)
	
		lEdge.sort()
		return (dNode, lEdge)

	def getRank(self, connect, sourceId, targetId):
		total = len(connect)
		l = connect[:]

		for i in range(total):
			gap = l[i] - sourceId
			if gap < 0: gap += sys.maxint
			l[i] = (gap, l[i])
	
		l.sort(key=operator.itemgetter(0), reverse=True)
		
		for i in range(total):
			if l[i][1] == targetId:
				return (i+1, total)
	
		return None

	# Sets locations of all nodes
	def setNodes(self, dNode, tFont, sFont, tHeight, sHeight):
		dNode[self.BEGIN_ID] = DepNode([self.BEGIN_ID, 'Root', 'root', 'ROOT', '_', -1, '_'])
		ids = dNode.keys()
		ids.sort()

		x = X_INIT
		for currId in ids:
			node = dNode[currId]
			x = self.setNode(x, node, tFont, sFont, tHeight, sHeight)

	# Called by setNodes
	def setNode(self, x, node, tFont, sFont, tHeight, sHeight):
		# form locations
		node.nX = x + H_GAP
		node.nY = Y_INIT
		node.nWidth  = tFont.measure(node.form)
		node.nHeight = tHeight
		
		m = len(node.connect) * E_GAP
		node.nMargin = max(0, m - node.nWidth)

		node.pWidth  = tFont.measure(node.pos)
		
		node.sId     = str(node.id - self.BEGIN_ID)
		node.sWidth  = sFont.measure(node.sId)
		node.sHeight = sHeight
		
		return node.nX + RATE_TOKEN * node.nWidth + node.nMargin

	# Prints all nodes
	def printNodes(self, dNode):
		ids = dNode.keys()
		ids.sort()
		
		for currId in ids:
			node = dNode[currId]
			self.printNode(node)

	# Called by printNodes
	def printNode(self, node):
		id = str(node.id)
		self.f_out.write(self.getNode('n'+id, node.nWidth+node.nMargin, node.nHeight, node.nX, node.nY, FONT_TOKEN_FAMILY, FONT_TOKEN_SIZE, 'plain', node.form))
		
		if B_POS:
			pX = node.nX + 0.5 * (node.nWidth - node.pWidth + node.nMargin)
			pY = node.nY + node.nHeight
			self.f_out.write(self.getNode('p'+id, node.pWidth, node.nHeight, pX, pY, FONT_TOKEN_FAMILY, FONT_TAG_SIZE, 'plain', node.pos))
		
		sX = node.nX + RATE_TOKEN * node.nWidth + 0.5 * node.nMargin
		sY = node.nY + 0.4 * node.nHeight
		self.f_out.write(self.getNode('s'+id, node.sWidth, node.sHeight, sX, sY, FONT_TOKEN_FAMILY, FONT_INDEX_SIZE, 'plain', node.sId))
		
	# fontSize : Integer
	# width, height, x, y : Float
	# id, fontFamily, fontStyle, token : String
	def getNode(self, id, width, height, x, y, fontFamily, fontSize, fontStyle, token):
		l = list()
	
		l.append('<node id="%s">' % (id))
		l.append('  <data key="d0">')
		l.append('  <y:ShapeNode>')
		l.append('    <y:Geometry width="%f" height="%f" x="%f" y="%f"/>' % (width, height, x, y))
		l.append('    <y:Fill hasColor="false"/>')
		l.append('    <y:BorderStyle hasColor="false" width="0.0"/>')
		l.append('    <y:NodeLabel alignment="center" autoSizePolicy="content" borderDistance="0.0" fontFamily="%s" fontSize="%d" fontStyle="%s" hasBackgroundColor="false" hasLineColor="false" modelName="internal" modelPosition="c" textColor="#000000" visible="true" x="0" y="0">%s</y:NodeLabel>' % (fontFamily, fontSize, fontStyle, token))
		l.append('    <y:Shape type="rectangle"/>')
		l.append('  </y:ShapeNode>')
		l.append('  </data>')
		l.append('</node>\n')
	
		return '\n'.join(l)

	def setEdges(self, dNode, lEdge, eFont):
		for t in lEdge:
			currId = t[0]
			headId = t[1]
			label  = t[2]
			cRank  = t[3]
			cTotal = t[4]
			hRank  = t[5]
			hTotal = t[6]
			vgap   = t[7]
			curr   = dNode[currId]
			head   = dNode[headId]
			cLoc   = (curr.nX, curr.nWidth+curr.nMargin)
			hLoc   = (head.nX, head.nWidth+head.nMargin)
			cMid   = cLoc[0] + float(cLoc[1])/2	
			hMid   = hLoc[0] + float(hLoc[1])/2	
		
			curr.eX1 = hLoc[0] + ((hRank*2-1) * float(hLoc[1]) / (hTotal*2))
			curr.eX2 = cLoc[0] + ((cRank*2-1) * float(cLoc[1]) / (cTotal*2))
			curr.eSX = curr.eX1 - hMid
			curr.eTX = curr.eX2 - cMid
			curr.eY  = Y_INIT - (vgap * V_GAP)
			
			m = eFont.measure(curr.label) - abs(curr.eX1 - curr.eX2)
			if m > 0:
				if currId > headId:
					curr.eX2 += m
					fstId = currId
				else:
					curr.eX1 += m
					fstId = headId
					
				for id in dNode:
					if id >= fstId: dNode[id].nX += m
	
	def printEdges(self, dNode):
		ids = dNode.keys()
		ids.sort()
		
		for currId in ids[1:]:
			node = dNode[currId]
			self.f_out.write(self.getEdge(node.headId, node.id, node.eSX, node.eTX, node.eX1, node.eX2, node.eY, FONT_EDGE_FAMILY, FONT_EDGE_SIZE, 'plain', node.label))

	# sourceId, targetId, fontSize : Integer
	# sx, tx, x1, x2, y : Float
	# fontFamily, fontStyle, label : String
	def getEdge(self, sourceId, targetId, sx, tx, x1, x2, y, fontFamily, fontSize, fontStyle, label):
		l = list()
		
		l.append('<edge id="e%d" source="n%d" target="n%d">' % (targetId, sourceId, targetId))
		l.append('  <data key="d1">')
		l.append('  <y:PolyLineEdge>')
		l.append('    <y:Path sx="%f" sy="0.0" tx="%f" ty="0.0">' % (sx, tx))
		l.append('      <y:Point x="%f" y="%f"/>' % (x1, y))
		l.append('      <y:Point x="%f" y="%f"/>' % (x2, y))
		l.append('    </y:Path>')
		l.append('    <y:LineStyle color="#000000" type="line" width="1.0"/>')
		l.append('    <y:Arrows source="none" target="standard"/>')
		l.append('    <y:EdgeLabel alignment="center" backgroundColor="#FFFFFF" distance="0.0" fontFamily="%s" fontSize="%d" fontStyle="%s" hasLineColor="false" modelName="centered" modelPosition="center" preferredPlacement="anywhere" ratio="0.5" textColor="#000000" visible="true">%s</y:EdgeLabel>' % (fontFamily, fontSize, fontStyle, label))
		l.append('    <y:BendStyle smoothed="%s"/>' % (B_SMOOTH))
		l.append('  </y:PolyLineEdge>')
		l.append('  </data>')
		l.append('</edge>\n')
	
		return '\n'.join(l)

CoNLL2Graphml(sys.argv)
