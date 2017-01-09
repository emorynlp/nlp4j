#!/usr/bin/python
# Optimized for yEd 3.7.0.2
# Author: Jinho D. Choi (choijd@colorado.edu)
import Tkinter
import tkFont
import operator
import sys
import re

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
FONT_TOKEN_SIZE   = 14
FONT_TAG_SIZE     = 13
FONT_INDEX_SIZE   = 11

#B_FTAG = False
#B_CIDX = False
H_GAP  = 25
V_GAP  = 20
E_GAP  = 6
X_INIT = 10
Y_INIT = 10

if sys.platform == 'darwin': RATE_TOKEN = 1.0
else                       : RATE_TOKEN = 0.9

class TBNode:
    def __init__(self, tag):
        if tag.startswith('-'):
            self.tag = tag
        else:
            t = tag.split('-')
            l = [t[0]]
            for item in t[1:]:
                if item.isdigit(): l.append(item)

            self.tag = '-'.join(l)
    	    
        self.form     = None
        self.parent   = None
        self.children = list()
    
    def addChild(self, node):
        node.parent = self
        self.children.append(node)

class TBTree:
    # root : TBNode
    def __init__(self, root):
        self.nd_root     = root
        self.ls_terminal = list()

    def addTerminal(self, node):
        node.terminalId = len(self.ls_terminal)
        node.height = 0
        self.ls_terminal.append(node)

class TBReader:
    # tree delimiters: '(', ')', white spaces
    re_delim = re.compile('([()\s])')
    
    # treeFile : String
    # opens 'treeFile'.
    def __init__(self, treeFile):
        self.f_tree    = open(treeFile)
        self.ls_tokens = list()
        self.BEGIN_ID  = 0

    # closes the current Treebank file.
    def close(self):
        self.f_tree.close()
    
    # returns iteration.
    def __iter__(self):
        return self
    
    # returns the next tree : TBTree
    def next(self):
        tree = self.getTree()
        if tree: return tree
        else   : raise StopIteration

    def getTree(self):
        del self.ls_tokens[:]

        while True:
            token = self.__nextToken()
            if   not token   : return None    # end of the file
            elif token == '(': break          # loop until '(' is found

        root       = TBNode('TOP')            # dummy head
        tree       = TBTree(root)
        curr       = root 
        nBrackets  = 1
        
        while True:
            token = self.__nextToken()
            if nBrackets == 1 and token == 'TOP': continue
            
            if token == '(':      # token_0 = '(', token_1 = 'tags'
                nBrackets += 1
                tags = self.__nextToken()
                node = TBNode(tags)
                node.id = self.BEGIN_ID
                self.BEGIN_ID += 1
                curr.addChild(node)
                curr = node
            elif token == ')':    # token_0 = ')'
                nBrackets -= 1
                curr = curr.parent
            else:                 # token_0 = 'form'
                curr.form = token
                tree.addTerminal(curr)
            
            if nBrackets == 0:    # the end of the current tree
                return tree
       
        return None
    
    # called by 'getTree()'.
    def __nextToken(self):
        if not self.ls_tokens:
            line = self.f_tree.readline()           # get tokens from the next line
            
            if not line:                            # end of the file
                self.close()
                return None
            if not line.strip():                    # blank line
                return self.__nextToken()
            
            for tok in self.re_delim.split(line):   # skip white-spaces
                if tok.strip(): self.ls_tokens.append(tok)
            
        return self.ls_tokens.pop(0)

class CoNLL2Graphml:
	def __init__(self, args):
		self.initArgs(args)
		global Y_INIT
		
		root    = Tkinter.Tk()
		tFont   = tkFont.Font(family=FONT_TOKEN_FAMILY, size=FONT_TOKEN_SIZE)
		pFont   = tkFont.Font(family=FONT_TOKEN_FAMILY, size=FONT_TAG_SIZE)
	#	sFont   = tkFont.Font(family=FONT_TOKEN_FAMILY, size=FONT_INDEX_SIZE)
		tHeight = tFont.metrics('linespace')
		pHeight = pFont.metrics('linespace')
	#	sHeight = sFont.metrics('linespace')
		
		self.f_out.write(PREFIX)
		
		for tree in self.tb_reader:
			top = tree.nd_root.children[0]
			Y_INIT += self.getVgap(tree)
			self.setTerminals(tree, tFont, pFont, tHeight, pHeight)
			Y_INIT -= tree.ls_terminal[0].pHeight
			self.setPhrases(top, tFont, tHeight)
			
			self.printTerminals(tree)
			self.printEdges(top)
			self.printPhrases(top)
			
			Y_INIT += 100
		
		root.destroy()
		self.f_out.write(SUFFIX)
		self.f_out.close()

####################### Initialize Arguments #######################

	def initArgs(self, args):
		if len(args) < 3: self.printUsage(args[0])
		global H_GAP, V_GAP, B_SMOOTH, B_FTAG
		
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
		#	elif key == '-f'               : B_FTAG     = True; i += 1
		#	elif key == '-c'               : B_CIDX     = True; i += 1
			else                           : self.printUsage(args[0])
		
		if inputFile: self.tb_reader = TBReader(inputFile)
		else        : self.printUsage(args[0])
		
		if outputFile: self.f_out = open(outputFile, 'w')
		else         : self.f_out = open(inputFile+'.graphml', 'w')
		
	def printUsage(self, cmd):
		print 'Usage: %s -i <input_file> [-o <output_file> -h <horizontal_gap> -v <vertical_gap> -p -s]' % (cmd)
		print '- input_file     (required): dependency trees in text format'
		print '- output_file    (optional): dependency trees in Graphml format (default = \'input_file.graphml\')'
		print '- horizontal_gap (optional): gaps between tokens (default = 25)'
		print '- vertical_gap   (optional): gaps between edges (default = 30)'
		print '-p: if set, include part-of-speech tags (values in the 3rd column)'

		sys.exit(0)	

################## Get Data ##################

	def getVgap(self, tree):
		m = 0
		for node in tree.ls_terminal:
			height = 0
			parent = node.parent
			while parent.tag != 'TOP':
				height += 1
				parent = parent.parent
			
			m = max(m, height)
		
		return m * V_GAP

	# Sets locations of all nodes
	def setTerminals(self, tree, tFont, sFont, tHeight, sHeight):
		x = X_INIT
		for node in tree.ls_terminal:
			x = self.setTerminal(x, node, tFont, sFont, tHeight, sHeight)

	# Called by setNodes
	def setTerminal(self, x, node, tFont, pFont, tHeight, pHeight):
		# form locations
		node.nX = x + H_GAP
		node.nY = Y_INIT
		node.nWidth  = tFont.measure(node.form)
		node.nHeight = tHeight

		parent = node.parent
		m = node.nWidth
		while parent.tag != 'TOP':
			if len(parent.children) == 1:
				width = tFont.measure(parent.tag)
				m = max(m, width)
			parent = parent.parent
		
		node.nMargin = max(0, m - node.nWidth)
		node.center  = node.nX + float(node.nWidth+node.nMargin)/2

		node.pWidth  = pFont.measure(node.tag)
		node.pHeight = pHeight
		
		'''
		node.sId     = str(node.terminalId + 1)
		node.sWidth  = sFont.measure(node.sId)
		node.sHeight = sHeight
		'''
		
		return node.nX + RATE_TOKEN * node.nWidth + node.nMargin

	def setPhrases(self, node, tFont, tHeight):
		if node.children:
			height = 0
			for child in node.children:
				self.setPhrases(child, tFont, tHeight)
				height = max(height, child.height)
			
			for child in node.children:
				child.height = height
				
			fst = node.children[0]
			lst = node.children[-1]
			node.height  = height + 1
			node.nWidth  = tFont.measure(node.tag)
			node.nHeight = tHeight
			node.center  = fst.center + float(lst.center-fst.center)/2
			node.nX      = node.center - float(node.nWidth)/2
		#	node.nY      = Y_INIT - (node.height * V_GAP)

	# Prints all nodes
	def printTerminals(self, tree):
		for node in tree.ls_terminal:
			self.printTerminal(node)

	# Called by printNodes
	def printTerminal(self, node):
		id = str(node.id)
		self.f_out.write(self.getNode('n'+id, node.nWidth+node.nMargin, node.nHeight, node.nX, node.nY, FONT_TOKEN_FAMILY, FONT_TOKEN_SIZE, 'plain', node.form))
		
		pX = node.nX + 0.5 * (node.nWidth - node.pWidth + node.nMargin)
		pY = node.nY - node.pHeight
		self.f_out.write(self.getNode('p'+id, node.pWidth, node.nHeight, pX, pY, FONT_TOKEN_FAMILY, FONT_TAG_SIZE, 'plain', node.tag))
		
		'''
		sX = node.nX + RATE_TOKEN * node.nWidth + 0.5 * node.nMargin
		sY = node.nY + 0.4 * node.nHeight
		self.f_out.write(self.getNode('s'+id, node.sWidth, node.sHeight, sX, sY, FONT_TOKEN_FAMILY, FONT_INDEX_SIZE, 'plain', node.sId))
		'''

	def printPhrases(self, node):
		if node.children:
			node.nY = Y_INIT - (node.height * V_GAP)
			self.printPhrase(node)
			
			for child in node.children:
				self.printPhrases(child)

	def printPhrase(self, node):
		id = str(node.id)
		self.f_out.write(self.getNode('n'+id, node.nWidth, node.nHeight, node.nX, node.nY, FONT_TOKEN_FAMILY, FONT_TAG_SIZE, 'plain', node.tag))
		
		nY = node.nY + node.nHeight
		for i,child in enumerate(node.children):
			self.f_out.write(self.getNode('n'+id+'-'+str(i), 0, 0, child.center, nY, FONT_TOKEN_FAMILY, FONT_TAG_SIZE, 'plain', ''))
		
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

	def printEdges(self, node):
		for i, child in enumerate(node.children):
			if child.children: pre = 'n'
			else             : pre = 'p'
			self.f_out.write(self.getVEdge('n'+str(node.id)+'-'+str(i), pre, child.id))
			
			if len(node.children) > 1:
				bId = 'n'+str(node.id)+'-0'
				eId = 'n'+str(node.id)+'-'+str(len(node.children)-1)
				self.f_out.write(self.getHEdge(bId, eId))
			
			self.printEdges(child)
			
	def getVEdge(self, sourceId, pre, targetId):
		l = list()
		
		l.append('<edge id="e%d" source="%s" target="%s%d">' % (targetId, sourceId, pre, targetId))
		l.append('  <data key="d1">')
		l.append('  <y:PolyLineEdge>')
		l.append('    <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>')
		l.append('    <y:LineStyle color="#000000" type="line" width="1.0"/>')
		l.append('    <y:Arrows source="none" target="none"/>')
		l.append('    <y:BendStyle smoothed="false"/>')
		l.append('  </y:PolyLineEdge>')
		l.append('  </data>')
		l.append('</edge>')
		
		return '\n'.join(l)

	def getHEdge(self, sourceId, targetId):
		l = list()
		
		l.append('<edge id="%s" source="%s" target="%s">' % (sourceId+targetId, sourceId, targetId))
		l.append('  <data key="d1">')
		l.append('  <y:PolyLineEdge>')
		l.append('    <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>')
		l.append('    <y:LineStyle color="#000000" type="line" width="1.0"/>')
		l.append('    <y:Arrows source="none" target="none"/>')
		l.append('    <y:BendStyle smoothed="false"/>')
		l.append('  </y:PolyLineEdge>')
		l.append('  </data>')
		l.append('</edge>')
		
		return '\n'.join(l)

CoNLL2Graphml(sys.argv)
