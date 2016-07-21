# Semantic Role Labeling

Our semantic role labeler uses a higher-order argument pruning algorithm that significantly improves recall from the first-order argument pruning algorithm, yet keeps a similar labeling complexity in practice. Our labeler takes about 0.45 milliseconds for labeling all arguments of each predicate on an Intel Xeon 2.57GHz machine and shows state-of-the-art accuracy compared to other dependency-based labeling approaches.

* [Transition-based Semantic Role Labeling Using Predicate Argument Clustering](http://aclweb.org/anthology/W11-0906), Jinho D. Choi, Martha Palmer, In Proceedings of the ACL Workshop on Relational Models of Semantics (RELMS'11), 37–45, 2011.
