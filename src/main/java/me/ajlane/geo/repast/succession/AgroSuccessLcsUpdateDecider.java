package me.ajlane.geo.repast.succession;

public class AgroSuccessLcsUpdateDecider implements LcsUpdateDecider {
  CodedLcsTransitionMap transMap;
  
  AgroSuccessLcsUpdateDecider(CodedLcsTransitionMap transMap) {
    this.transMap = transMap;    
  }

  @Override
  public LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState, Integer timeInState,
      CodedEnvrConsequent targetEnvrTrans) {
    // TODO Auto-generated method stub
    return null;
  }  

}
