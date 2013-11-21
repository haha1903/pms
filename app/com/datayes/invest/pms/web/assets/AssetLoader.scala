package com.datayes.invest.pms.web.assets

trait AssetLoader {
  def load(): Option[models.AssetCommon]
}