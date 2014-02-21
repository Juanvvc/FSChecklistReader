#!/usr/bin/python
# -*- coding: utf-8 -*-

# This script converts from a simple checklist format to the .xml format used in FlightGear

# Copyright (C) 2014 Juan Vera juanvvc@gmail.com
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

# Usage: python checklist2xml.py inputfile outputfile

import sys

if len(sys.argv) < 3:
    print 'Usage: python %0 inputfile outputfile'%sys.argv[0]
    sys.exit(1)

inf = open(sys.argv[1], 'r')
outf = open(sys.argv[2], 'w')

# header of the xml file
outf.write('<?xml version="1.0" encoding="UTF-8" ?>\n')
outf.write('<PropertyList>\n')

new_checklist = True
num_checklists = 0

for line in inf.readlines():
    line = line.strip()

    # if an empty line is found, start a new checklist
    if len(line) == 0:
        new_checklist = True

        if num_checklists > 0:
            # end the current checklist
            outf.write('  </checklist>\n')
    else:
        if new_checklist:
            # start a new checklist. The line is the title
            outf.write('  <checklist>\n    <title>%s</title>\n'%line)
            new_checklist = False
            num_checklists += 1
        else:
            # start a new item
            name, value = line.split('|', 2)
            name = name.strip()
            value = value.strip()
            if name.startswith('*'):
                # if name starts with a *, it is not doable. Remove the star
                name = name[1:]
                outf.write('    <item doable="false">\n')
            else:
                outf.write('    <item>\n')
            outf.write("      <name>%s</name>\n      <value>%s</value>\n    </item>\n"%(name, value))

# end of the file
if num_checklists > 0:
    outf.write('  </checklist>\n')
outf.write('</PropertyList>\n')
outf.close()
inf.close()

