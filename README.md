# simple-json-diff
简单的自用JSON差异比较
基于 alibaba fastjson 的比较工具
很简单的思路就是将json的每一个key的数据进行递归比较
<pre><code>
public static void main(String[] args) {
    DiffJson diffJson = new DiffJson();
    Map<String, DiffNode> diffNodeMap = diffJson.diff("{\"test\":3, \"haha\":4, \"tongzi\":[[[1,3],2,{\"hahaha\":33333333}],{\"22\":2},{\"33\":4}]}",
            "{\"test\":1.5, \"test2\":666, \"tongzi\":[[[1,4],2,{\"hahaha\":33333333}],{\"22\":2},{\"33\":3}]}");
    String result = diffJson.toStringDiffNode(diffNodeMap);
    System.out.println(result);
}
</code></pre>
<pre><code>
参数 haha 原值为 4 调整为 null，参数 test2 原值为 null 调整为 666，参数 test 原值为 3 调整为 1.5，参数 tongzi[0][0][1] 原值为 3 调整为 4，参数 tongzi[2].33 原值为 4 调整为 3
</code></pre>
