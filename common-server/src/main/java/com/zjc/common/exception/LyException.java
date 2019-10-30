package com.zjc.common.exception;

import com.zjc.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
- @Description: 自定义异常
*/
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LyException extends RuntimeException{

  private ExceptionEnum exceptionEnum;

}