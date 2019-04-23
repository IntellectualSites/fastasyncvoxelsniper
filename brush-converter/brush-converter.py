import os

def main():
  print("Deleting old files...")
  if os.path.exists("brushes-converted.txt"):
    if os.path.isfile("brushes-converted.txt"):
      os.remove("brushes-converted.txt")
    else:
      os.removedirs("brushes-converted.txt")
  print("Checking existing files...")
  if not os.path.exists("brushes.txt") or not os.path.isfile("brushes.txt"):
    print("Brushes file does not exist or it is a folder.")
    end()
    return
  contents = open("brushes.txt", "r").readlines()
  empty = True
  for content in contents:
    if content.strip():
      empty = False
  if empty:
    print("Brushes file is empty.")
    end()
    return
  print("Converting brushes...")
  newContents = open("brushes-converted.txt", "w+")
  for content in contents:
    newContents.write("register" + content.split()[0] + "();\n")
  newContents.write("\n")
  for content in contents:
    splittedContent = content.split()
    brushClass = splittedContent[0]
    brushName = brushClass.replace("Brush", "")
    splittedContent.pop(0)
    brushAliases = splittedContent
    toWrite = "private void register" + brushClass + "() {"
    toWrite += "\n"
    toWrite += "    BrushProperties properties = BrushProperties.builder()"
    toWrite += "\n"
    toWrite += "        .name(\"" + brushName + "\")"
    toWrite += "\n"
    toWrite += "        .permission(\"voxelsniper.brush." + brushName.lower() + "\")"
    for brushAlias in brushAliases:
      toWrite += "\n"
      toWrite += "        .alias(\"" + brushAlias + "\")"
    toWrite += "\n"
    toWrite += "        .creator(" + brushClass + "::new)"
    toWrite += "\n"
    toWrite += "        .build();"
    toWrite += "\n"
    toWrite += "    this.registry.register(properties);"
    toWrite += "\n"
    toWrite += "}"
    toWrite += "\n"
    toWrite += "\n"
    newContents.write(toWrite)
  newContents.close()
  print("Brushes converted.")
  end()

def end():
  os.system("pause")

if __name__ == "__main__":
  main()