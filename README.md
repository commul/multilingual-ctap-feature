# ctap-features

This project is a module of the Common Text Analysis Platform, or CTAP system,
which can be accessed from https://kommul.eurac.edu/ctapWebApp 

The CTAP project contains two components: a Web frontend and the feature
extractors backend. The present project is the latter component.

In its current state, this feature extractors backend contains analysis engines for calculating linguistic complexity measures for texts in English, German and Italian.

The project contains analysis engines written in the classic
[UIMA](https://uima.apache.org/) manner---each AE is made up of two parts: an XML descriptor and an accompanying class file. This engines are usable in themselves. However, they are designed to be imported into the CTAP systems. So certain conventions are followed. The functions and analysis that the annotators and analysis engines realize are quite self-explanatory by looking at their names and the description in the XML file.

The CTAP system uses these analysis engines to create feature extraction
pipelines, which mimics the process of creating an Aggregate Analysis Engine in
UIMA. The CTAP system then calls these pipelines based on user choices of the
textual features they want for analyzing their corpora. As a result, the
analysis engines included in this package are the backbones of the CTAP system.
They determine the analytical functionalities of the system.  

Users are free to use the analysis engines in this project for their own
projects. If anybody is interested in helping us expand the collection of
analysis engines, please contact the LT group at applinglt@scientificnet.onmicrosoft.com

This software is licensed under the BSD License.


```
@ARTICLE{chenmeurersctap,
       author = {{Chen}, X. and {Meurers}, D.},
        title = "{CTAP: A Web-Based Tool Supporting Automatic Complexity Analysis}",
      volume = {Proceedings of the Workshop on Computational Linguistics for Linguistic Complexity (CL4LC)},
     keywords = {linguistic complexity - readability - Italian - text analysis - cross-lingual analysis},
         year = 2016,
        month = December,
       adsurl = {https://www.aclweb.org/anthology/W16-4113}
}
```

```
@ARTICLE{okininafreyweissctap,
       author = {{Okinina}, N., {Frey}, J. C. and {Weiss}, Z.},
        title = "{CTAP for Italian: Integrating Components for the Analysis of Italian into a Multilingual Linguistic Complexity Analysis Tool}",
      volume = {Proceedings of the Twelfth International Conference on Language Resources and Evaluation (LREC 2020)},
     keywords = {linguistic complexity - readability - Italian - text analysis - cross-lingual analysis},
         year = 2020,
        month = May
}
```
