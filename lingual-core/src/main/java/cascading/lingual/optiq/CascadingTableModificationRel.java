/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.lingual.optiq;

import java.util.List;

import cascading.lingual.catalog.TableDef;
import cascading.lingual.optiq.meta.Branch;
import org.eigenbase.oj.stmt.OJPreparingStmt;
import org.eigenbase.rel.RelNode;
import org.eigenbase.rel.TableModificationRelBase;
import org.eigenbase.relopt.RelOptCluster;
import org.eigenbase.relopt.RelOptTable;
import org.eigenbase.relopt.RelTraitSet;

/**
 *
 */
public class CascadingTableModificationRel extends TableModificationRelBase implements CascadingRelNode
  {
  public CascadingTableModificationRel( RelOptCluster cluster, RelTraitSet traits, RelOptTable table,
                                        OJPreparingStmt.CatalogReader catalogReader, RelNode child,
                                        Operation operation, List<String> updateColumnList, boolean flattened )
    {
    super( cluster, traits, table, catalogReader, child, operation, updateColumnList, flattened );
    }

  @Override
  public RelNode copy( RelTraitSet traitSet, List<RelNode> inputs )
    {
    assert inputs.size() == 1;
    return new CascadingTableModificationRel(
      getCluster(),
      traitSet,
      getTable(),
      getCatalogReader(),
      inputs.get( 0 ),
      getOperation(),
      getUpdateColumnList(),
      isFlattened() );
    }

  @Override
  public Branch visitChild( Stack stack )
    {
    RelNode child = getChild();
    Branch branch = ( (CascadingRelNode) child ).visitChild( stack );

    TableDef tableDef = branch.platformBroker.getCatalog().resolveTableDef( getTable().getQualifiedName() );

    return new Branch( branch, tableDef.getName(), tableDef.getIdentifier() );
    }
  }
