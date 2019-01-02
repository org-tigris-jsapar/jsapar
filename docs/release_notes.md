---
layout: page
title: Release notes
---
#Release notes
## 2.0
### 2.0.1
1. Performance improvements while parsing delimited (CSV) sources. Performance in normal scenario should be improved 
by at least 50%. Both CPU and memory impact has been significantly improved.
1. Default cell cache size while parsing is now 1 (instead of 10). 
1. Changed behaviour when parsing quoted delimited sources. When a start quote is found but no end quote within 8k of data, the parser now
tries again to parse the source but considers that particular cell as not being quoted.
