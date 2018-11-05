-- Breadth-first preorder for multibranching trees, “rose trees”.

data Tree a = MkTree {value :: a, children :: [Tree a] }

instance Show a => Show (Tree a) where
  show (MkTree v cs) = show v ++ "\n" ++ concatMap (\c -> "  " ++ show c) cs

preorder :: Tree a -> [a]
preorder (MkTree v cs) = v : concatMap preorder cs
-- Consequently, preorder (MkTree v []) = [v]

{-
              1 → 2 → ⟨21, 22, 23⟩
	        → 3 → ⟨31, 4:⟨341, 342⟩ ⟩
		→ 5 → ⟨⟩
-}

test :: Tree String
test = MkTree "1"
       [
         MkTree "  2"  [ node "    21", node "    22", node "    23"]
       , MkTree "  3"  [ node "    31", MkTree "    4" [node "     341", node "     342"]]
       , node "  5"
       ]
       where node n = MkTree n [] 
