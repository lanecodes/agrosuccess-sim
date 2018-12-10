package me.ajlane.geo.repast.succession;

public interface LcsUpdateDecider {
  LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState, Integer timeInState,
      CodedEnvrConsequent targetEnvrTrans);
  
}
