data Tree a = MkTree {value :: a, children :: [Tree a] }

instance Show a => Show (Tree a) where
  show (MkTree v cs) = show v ++ "\n" ++ concatMap (\c -> "  " ++ show c) cs

preorder :: Tree a -> [a]
preorder (MkTree v []) = [v] 
preorder (MkTree v cs) = v : (concat (map preorder cs))

{-
              1 → 2 → ⟨21, 22, 23⟩
	        → 3 → ⟨31, 4:⟨341, 342⟩ ⟩
		→ 5 → ⟨⟩
-}

test :: Tree String
test = MkTree "1"
       [
         MkTree "  2"  [ MkTree "    21" [], MkTree "    22" [], MkTree "    23" []]
       , MkTree "  3"  [ MkTree "    31" [], MkTree "    4" [node "     341", node "     342"]]
       , MkTree "  5"  []
       ]
       where node n = MkTree n [] 
