package cachemesh.core.config.support;

import java.util.Map;

import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Mapper;
import cachemesh.common.config2.Path;
import cachemesh.common.config2.Type;
import cachemesh.common.config2.types.BeanType;
import cachemesh.common.config2.types.ListType;
import cachemesh.core.TransportRegistry;
import cachemesh.core.config.InlineNodesConfig;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.config.NodesConfig;
import lombok.Getter;

@Getter
public class NodesConfigMapper implements Mapper<NodesConfig> {

    private final ListType<NodeConfig> nodeConfigListType;

    public NodesConfigMapper(TransportRegistry transportRegistry) {
        Type<NodeConfig> nodeConfigType = new BeanType<>(NodeConfig.class, new NodeConfigMapper(transportRegistry));
        this.nodeConfigListType = new ListType<>(nodeConfigType);
    }

    @Override
    public NodesConfig toBean(MapContext ctx, Path path, Object parent, Map<String, Object> propValues) {
        String kind = (String)propValues.get("kind");

        NodesConfig r = null;
        if (kind.equals("inline")) {
            InlineNodesConfig c = new InlineNodesConfig();
            c.setKind("inline");

            var inlineNodes = getNodeConfigListType().convert(ctx, Path.of(path, "inline"), r, propValues.get("inline"));
            c.setInline(inlineNodes);

            r = c;
        }

        return r;
    }
    
}
