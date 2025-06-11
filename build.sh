# baut das image und pusht es in die registry
docker buildx build --platform linux/arm64 -t simonkub/availability:latest --push .