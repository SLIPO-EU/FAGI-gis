INSERT INTO fused_geometries (subject_A, subject_B, geom) SELECT links.nodea, links.nodeb, dataset_a_geometries.geom from links INNER JOIN dataset_a_geometries ON (links.nodea = dataset_a_geometries.subject)