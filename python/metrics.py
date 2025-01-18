import matplotlib.pyplot as plt

# Extracting updated data for the additional plots including Time Average
k_values = [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100]
bm25_precision = [0.2678, 0.2126, 0.1858, 0.1660, 0.1524, 0.1434, 0.1378, 0.1335, 0.1272, 0.1223, 0.1179, 0.1148, 0.1115, 0.1086, 0.1057, 0.1029, 0.0996, 0.0974, 0.0958, 0.0937]
bm25_recall = [0.1078, 0.1444, 0.1688, 0.1918, 0.2134, 0.2232, 0.2377, 0.2575, 0.2666, 0.2751, 0.2918, 0.3046, 0.3103, 0.3170, 0.3249, 0.3286, 0.3324, 0.3480, 0.3525, 0.3573]
bm25_ndcg = [0.2938, 0.2468, 0.2215, 0.2026, 0.1888, 0.1790, 0.1720, 0.1663, 0.1596, 0.1542, 0.1493, 0.1454, 0.1416, 0.1382, 0.1348, 0.1316, 0.1282, 0.1256, 0.1235, 0.1211]
bm25_time_avg = [
    0.4443918671328671, 0.34910662937062936, 0.30737095104895107, 0.3066145244755245,
    0.29671823776223777, 0.32820136363636365, 0.306264006993007, 0.33453786013986014,
    0.3261858601398601, 0.3298324195804196, 0.321159006993007, 0.3225446083916084,
    0.3347252727272727, 0.3211261888111888, 0.3271722027972028, 0.32258011888111887,
    0.3383382727272727, 0.32938671328671326, 0.33857225174825173, 0.3421007272727273
]
mpnet_precision = [0.3399, 0.2888, 0.2555, 0.2280, 0.2131, 0.1960, 0.1834, 0.1715, 0.1630, 0.1558, 0.1498, 0.1442, 0.1389, 0.1339, 0.1290, 0.1252, 0.1204, 0.1159, 0.1124, 0.1105]
mpnet_recall = [0.1249, 0.1831, 0.2212, 0.2501, 0.2754, 0.2938, 0.3167, 0.3254, 0.3365, 0.3560, 0.3679, 0.3769, 0.3872, 0.3966, 0.4020, 0.4133, 0.4174, 0.4225, 0.4300, 0.4408]
mpnet_ndcg = [0.3604, 0.3259, 0.3002, 0.2757, 0.2594, 0.2435, 0.2301, 0.2181, 0.2091, 0.2017, 0.1949, 0.1883, 0.1826, 0.1768, 0.1712, 0.1667, 0.1616, 0.1566, 0.1529, 0.1505]
mpnet_time_avg = [
    0.9443782167832168, 1.248792, 1.550018034965035, 1.6187424545454545,
    1.9117455944055946, 2.090634888111888, 2.195523041958042, 2.245659713286713,
    2.5106509370629366, 2.635584832167832, 2.9513502447552447, 2.874297202797203,
    2.8788385454545455, 2.9656244685314683, 3.2310833776223777, 3.289484867132867,
    3.632890104895105, 3.546416993006993, 3.4907313706293706, 3.8030279370629367
]
minilm_precision = [0.2909, 0.2559, 0.2247, 0.2080, 0.1933, 0.1793, 0.1666, 0.1577, 0.1473, 0.1371, 0.1316, 0.1284, 0.1242, 0.1191, 0.1149, 0.1098, 0.1060, 0.1023, 0.1003, 0.0973]
minilm_recall = [0.0911, 0.1412, 0.1815, 0.2124, 0.2332, 0.2507, 0.2627, 0.2833, 0.2915, 0.2964, 0.3087, 0.3245, 0.3334, 0.3451, 0.3551, 0.3594, 0.3650, 0.3722, 0.3814, 0.3866]
minilm_ndcg = [0.3129, 0.2874, 0.2646, 0.2512, 0.2376, 0.2239, 0.2098, 0.2016, 0.1913, 0.1804, 0.1746, 0.1699, 0.1647, 0.1590, 0.1546, 0.1492, 0.1449, 0.1408, 0.1380, 0.1347]
minilm_time_avg = [
    0.63048806993007, 0.8279912447552447, 1.028444062937063, 1.1748281048951048,
    1.2910926643356644, 1.4023866993006995, 1.4767546923076922, 1.5708129160839162,
    1.7643370699300698, 1.8171357622377624, 1.9462144545454545, 2.143514321678322,
    2.14944606993007, 2.24468448951049, 2.2867182167832167, 2.3203700279720283,
    2.240285846153846, 2.282490370629371, 2.4050976293706294, 2.3748735384615385
]
multi_qa_precision = [0.2895, 0.2524, 0.2350, 0.2101, 0.1952, 0.1825, 0.1712, 0.1593, 0.1526, 0.1436, 0.1372, 0.1307, 0.1261, 0.1219, 0.1170, 0.1130, 0.1098, 0.1065, 0.1039, 0.1007]
multi_qa_recall = [0.0851, 0.1350, 0.1885, 0.2114, 0.2447, 0.2583, 0.2756, 0.2823, 0.2995, 0.3118, 0.3157, 0.3208, 0.3357, 0.3462, 0.3552, 0.3585, 0.3606, 0.3672, 0.3810, 0.3854]
multi_qa_ndcg = [0.3123, 0.2882, 0.2735, 0.2507, 0.2396, 0.2270, 0.2157, 0.2038, 0.1970, 0.1881, 0.1806, 0.1734, 0.1679, 0.1632, 0.1583, 0.1534, 0.1495, 0.1457, 0.1425, 0.1388]
multi_qa_time_avg = [
    0.9787715314685315, 1.350944902097902, 1.6735780699300697, 1.9699312727272726,
    2.196051041958042, 2.3657167412587414, 2.7614359090909093, 2.705128825174825,
    2.8675034685314684, 3.1287147342657344, 3.1789854545454546, 3.4012916573426573,
    3.6191494965034967, 3.6746136293706293, 3.7554495804195804, 3.9063155104895104,
    4.05188227972028, 4.25115437062937, 4.3892129720279724, 4.404580713286713
]

# Updated metrics including Time Average
metrics = ["NDCG", "Precision", "Recall", "Time Average"]
methods = ["BM25", "all-mpnet-base-v2", "all-MiniLM-L6-v2", "multi-qa-mpnet-base-dot-v1"]
all_metrics = [
    {"NDCG": bm25_ndcg, "Precision": bm25_precision, "Recall": bm25_recall, "Time Average": bm25_time_avg},
    {"NDCG": mpnet_ndcg, "Precision": mpnet_precision, "Recall": mpnet_recall, "Time Average": mpnet_time_avg},
    {"NDCG": minilm_ndcg, "Precision": minilm_precision, "Recall": minilm_recall, "Time Average": minilm_time_avg},
    {"NDCG": multi_qa_ndcg, "Precision": multi_qa_precision, "Recall": multi_qa_recall, "Time Average": multi_qa_time_avg}
]

# Plot each updated metric
# for metric in metrics:
#     plt.figure(figsize=(10, 6))
#     for i, method in enumerate(methods):
#         plt.plot(k_values, all_metrics[i][metric], label=method)
#     plt.title(f"{metric} @K Average (BM25 vs all-mpnet-base-v2 vs all-MiniLM-L6-v2 vs multi-qa-mpnet-base-dot-v1)")
#     plt.xlabel("K")
#     plt.ylabel(metric)
#     plt.legend()
#     plt.grid(True)
#     plt.show()

# Plot Time Average
plt.figure(figsize=(10, 6))
for i, method in enumerate(methods):
    plt.plot(k_values, all_metrics[i]["Time Average"], label=method)
plt.title(f"Time Average @K")
plt.xlabel("K")
plt.ylabel("Time Average (Seconds)")
plt.legend()
plt.grid(True)
plt.savefig("metrics/time_avg.png")


# Plot precision, recall, and NDCG
plt.figure(figsize=(10, 6))
plt.title("Precision@K")
for i, method in enumerate(methods):
    plt.plot(k_values, all_metrics[i]["Precision"], label=method)
plt.xlabel("K")
plt.ylabel("Precision")
plt.legend()
plt.grid(True)
plt.savefig("metrics/precision_avg.png")

plt.figure(figsize=(10, 6))
plt.title("Recall@K")
for i, method in enumerate(methods):
    plt.plot(k_values, all_metrics[i]["Recall"], label=method)
plt.xlabel("K")
plt.ylabel("Recall")
plt.legend()
plt.grid(True)
plt.savefig("metrics/recall_avg.png")

plt.figure(figsize=(10, 6))
plt.title("NDCG@K")
for i, method in enumerate(methods):
    plt.plot(k_values, all_metrics[i]["NDCG"], label=method)
plt.xlabel("K")
plt.ylabel("NDCG")
plt.legend()
plt.grid(True)
plt.savefig("metrics/ndcg_avg.png")


