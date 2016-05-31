# Kanpeki
Lightweight library for parsing log files

### Status
[![Build Status](https://travis-ci.org/HenriqueMCastro/kanpeki.png)](https://travis-ci.org/HenriqueMCastro/kanpeki)

### Properties

files.path => The path where the files to processed are. The wildcard '**' can be used at
  the end of the path and only at the end. If used all subfolders (and their children)
  will be processed.

files.format => default "*" (all files). Used to process only some files based on a filter.
Wildcards can be used.

manage.offsets.enabled => default false, set to true if offsets for each file processed
  should be saved to disk. This way if the application goes down then on restart it can
  pick up from where it left off.

offsets.db.path => Path to write the offset database to.
