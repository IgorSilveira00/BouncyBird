package com.aor.bouncy.view.entities;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.model.entities.EntityModel;

import java.util.HashMap;
import java.util.Map;

import static com.aor.bouncy.model.entities.EntityModel.ModelType.*;

/**
 * By: arestivo
 * A factory for EntityView objects with cache
 */

public class ViewFactory {
    private static Map<EntityModel.ModelType, EntityView> cache =
            new HashMap<EntityModel.ModelType, EntityView>();

    public static EntityView makeView(MyBouncyBird game, EntityModel model) {
        if (!cache.containsKey(model.getType())) {
            if (model.getType() == SPIKE) cache.put(model.getType(), new SpikeView(game));
            if (model.getType() == RIGHT_SPIKE) cache.put(model.getType(), new SpikeView(game));
            if (model.getType() == LEFT_SPIKE) cache.put(model.getType(), new SpikeView(game));
            if (model.getType() == BIRD) cache.put(model.getType(), new BirdView(game));
            if (model.getType() == EDGE) cache.put(model.getType(), new EdgeView(game));
            if (model.getType() == BONUS) cache.put(model.getType(), new BonusView(game));
        }
        return cache.get(model.getType());
    }
}