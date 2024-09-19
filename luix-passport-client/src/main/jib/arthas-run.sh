# check if the arthas-boot.jar exists
if [ ! -f arthas-boot.jar ]; then
  # download arthas-boot.jar if it does not exist
  curl -O https://arthas.aliyun.com/arthas-boot.jar
fi
# run arthas
java -jar arthas-boot.jar